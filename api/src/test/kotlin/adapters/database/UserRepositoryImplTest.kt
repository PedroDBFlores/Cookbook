package adapters.database

import adapters.database.schema.Users
import config.Dependencies
import errors.UserNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.User
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import ports.HashingService
import ports.PasswordMismatchError
import utils.DTOGenerator.generateUser
import java.sql.SQLException

class UserRepositoryImplTest : DescribeSpec({
    val database = Dependencies.database

    beforeSpec {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    afterTest {
        transaction(database) {
            Users.deleteAll()
        }
    }

    val basicHashingService = mockk<HashingService> {
        every { hash(any()) } answers {
            "${firstArg<String>()}HASH"
        }
        every { verify(any(), any()) } answers {
            firstArg<String>() == secondArg<String>().replace("HASH", "")
        }
    }

    fun createUser(userPassword: String): User {
        val user = generateUser(id = 0)
        val repo = UserRepositoryImpl(database = database, hashingService = basicHashingService)
        val id = repo.create(user = user, userPassword = userPassword)
        return user.copy(id = id)
    }

    describe("User repository") {
        describe("find") {
            it("finds a user by id ") {
                val expectedUser = createUser("PASSWORD")
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val user = repo.find(id = expectedUser.id)

                user.shouldNotBeNull()
                user.shouldBe(expectedUser.copy(passwordHash = "PASSWORDHASH"))
            }

            it("finds a user by username") {
                val expectedUser = createUser("PASSWORD")
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val user = repo.find(userName = expectedUser.userName)

                user.shouldNotBeNull()
                user.shouldBe(expectedUser.copy(passwordHash = "PASSWORDHASH"))
            }
        }

        it("creates a user on the database") {
            val hashingService = mockk<HashingService> {
                every { hash("PASSWORD") } returns basicHashingService.hash("PASSWORD")
            }
            val repo = UserRepositoryImpl(database = database, hashingService = hashingService)
            val user = generateUser(id = 0)

            val id = repo.create(user = user, userPassword = "PASSWORD")

            id.shouldNotBeZero()
            verify(exactly = 1) { hashingService.hash("PASSWORD") }
        }

        describe("update") {
            it("update an user when no new password is provided") {
                val user = createUser("PASSWORD")
                val hashingService = mockk<HashingService>(relaxed = true)
                val repo = UserRepositoryImpl(database = database, hashingService = hashingService)
                repo.update(user = user)

                verify { hashingService wasNot called }
            }

            it("changes the password as well if both old and new password are provided") {
                val currentUser = createUser("OLDPASSWORD")
                println(currentUser.passwordHash)
                val hashingService = mockk<HashingService> {
                    every { verify("OLDPASSWORD", "OLDPASSWORDHASH") } returns basicHashingService.verify(
                        "OLDPASSWORD",
                        "OLDPASSWORDHASH"
                    )
                    every { hash("NEWPASSWORD") } returns basicHashingService.hash("NEWPASSWORD")
                }
                val repo = UserRepositoryImpl(database = database, hashingService = hashingService)

                repo.update(user = currentUser, oldPassword = "OLDPASSWORD", newPassword = "NEWPASSWORD")

                verify(exactly = 1) {
                    hashingService.verify("OLDPASSWORD", "OLDPASSWORDHASH")
                    hashingService.hash("NEWPASSWORD")
                }
            }

            it("throws if the user doesn't exist on the database") {
                val currentUser = createUser("OLDPASSWORD")
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val act = { repo.update(user = currentUser.copy(id = 99999)) }

                shouldThrow<UserNotFound> { act() }
            }

            it("throws if a new password is provided but the old is not") {
                val currentUser = createUser("OLDPASSWORD")
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val act = { repo.update(user = currentUser, oldPassword = null, newPassword = "newPassword") }

                shouldThrow<IllegalArgumentException> { act() }
            }

            it("throws if the old password doesn't match") {
                val currentUser = createUser("OLDPASSWORD")
                val repo = UserRepositoryImpl(database = database, hashingService = basicHashingService)

                val act = {
                    repo.update(
                        user = currentUser,
                        oldPassword = "notTheSamePassword",
                        newPassword = "newPassword"
                    )
                }

                shouldThrow<PasswordMismatchError> { act() }
            }
        }

        it("deletes a user") {
            val currentUser = createUser("PASSWORD")
            val repo = UserRepositoryImpl(database = database, hashingService = mockk())

            val deleted = repo.delete(currentUser.id)

            deleted.shouldBeTrue()
        }

        describe("User table constraints") {
            it("throws if a user with the same username exists") {
                val firstUser = createUser("PASSWORD")
                val secondUser = firstUser.copy(id = 0, name = "Mark")
                val repo = UserRepositoryImpl(database = database, hashingService = basicHashingService)

                val act = { repo.create(secondUser, "OTHERPASSWORD") }

                shouldThrow<SQLException> { act() }
            }
        }
    }
})