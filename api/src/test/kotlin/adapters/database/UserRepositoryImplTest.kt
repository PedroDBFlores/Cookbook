package adapters.database

import adapters.database.DatabaseTestHelper.createRole
import adapters.database.DatabaseTestHelper.createUser
import adapters.database.DatabaseTestHelper.mapToUser
import adapters.database.schema.Roles
import adapters.database.schema.UserRoles
import adapters.database.schema.Users
import errors.PasswordMismatchError
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
import java.sql.SQLException
import model.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.HashingService
import utils.DTOGenerator.generateUser

internal class UserRepositoryImplTest : DescribeSpec({
    val database = DatabaseTestHelper.database

    lateinit var role: Role

    beforeSpec {
        transaction(database) {
            SchemaUtils.create(Users, Roles, UserRoles)
            role = createRole("User", "USER")
        }
    }

    afterTest {
        transaction(database) {
            UserRoles.deleteAll()
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

    describe("User repository") {
        describe("find") {
            it("finds a user by id ") {
                val expectedUser =
                    createUser(userPassword = "PASSWORD", hashingService = basicHashingService)
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val user = repo.find(id = expectedUser.id)

                user.shouldNotBeNull()
                user.shouldBe(expectedUser.copy(passwordHash = "PASSWORDHASH", roles = listOf(RoleCodes.USER)))
            }

            it("finds a user by username") {
                val expectedUser =
                    createUser(userPassword = "PASSWORD", hashingService = basicHashingService)
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val user = repo.find(userName = expectedUser.username)

                user.shouldNotBeNull()
                user.shouldBe(expectedUser.copy(passwordHash = "PASSWORDHASH", roles = listOf(RoleCodes.USER)))
            }
        }

        describe("create") {
            it("creates a user on the database") {
                val hashingService = mockk<HashingService> {
                    every { hash("PASSWORD") } returns basicHashingService.hash("PASSWORD")
                }
                val repo = UserRepositoryImpl(database = database, hashingService = hashingService)
                val user = generateUser(id = 0)

                val id = repo.create(user = user, userPassword = "PASSWORD")

                id.shouldNotBeZero()
                val createdUser =
                    transaction(database) { Users.select { Users.id eq id }.map { row -> row.mapToUser() }.first() }
                createdUser.shouldBe(user.copy(id = id, passwordHash = basicHashingService.hash("PASSWORD")))
                verify(exactly = 1) { hashingService.hash("PASSWORD") }
            }

            it("has a default role of 'USER'") {
                val repo = UserRepositoryImpl(database = database, hashingService = mockk(relaxed = true))
                val user = generateUser(id = 0)

                val id = repo.create(user = user, userPassword = "PASSWORD")
                val row = transaction(database) {
                    UserRoles.select { (UserRoles.userId eq id) and (UserRoles.roleId eq role.id) }
                        .firstOrNull()
                }
                row.shouldNotBeNull()
            }
        }

        describe("update") {
            it("update an user when no new password is provided") {
                val createdUser =
                    createUser(userPassword = "PASSWORD", hashingService = basicHashingService)
                val hashingService = mockk<HashingService>(relaxed = true)
                val userToUpdate = createdUser.copy(name = "ABC")

                val repo = UserRepositoryImpl(database = database, hashingService = hashingService)
                repo.update(user = userToUpdate)

                val updatedUser = transaction(database) {
                    Users.select { Users.id eq createdUser.id }.map { row -> row.mapToUser() }.first()
                }
                updatedUser.shouldBe(userToUpdate.copy(passwordHash = basicHashingService.hash("PASSWORD")))
                verify { hashingService wasNot called }
            }

            it("changes the password as well if both old and new password are provided") {
                val currentUser =
                    createUser(userPassword = "OLDPASSWORD", hashingService = basicHashingService)
                val hashingService = mockk<HashingService> {
                    every { verify("OLDPASSWORD", "OLDPASSWORDHASH") } returns basicHashingService.verify(
                        "OLDPASSWORD",
                        "OLDPASSWORDHASH"
                    )
                    every { hash("NEWPASSWORD") } returns basicHashingService.hash("NEWPASSWORD")
                }
                val repo = UserRepositoryImpl(database = database, hashingService = hashingService)

                repo.update(user = currentUser, oldPassword = "OLDPASSWORD", newPassword = "NEWPASSWORD")
                val updatedUser = transaction(database) {
                    Users.select { Users.id eq currentUser.id }.map { row -> row.mapToUser() }.first()
                }

                updatedUser.shouldBe(currentUser.copy(passwordHash = basicHashingService.hash("NEWPASSWORD")))
                verify(exactly = 1) {
                    hashingService.verify("OLDPASSWORD", "OLDPASSWORDHASH")
                    hashingService.hash("NEWPASSWORD")
                }
            }

            it("throws if the user doesn't exist on the database") {
                val currentUser =
                    createUser(userPassword = "OLDPASSWORD", hashingService = basicHashingService)
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val act = { repo.update(user = currentUser.copy(id = 99999)) }

                shouldThrow<UserNotFound> { act() }
            }

            it("throws if a new password is provided but the old is not") {
                val currentUser =
                    createUser(userPassword = "OLDPASSWORD", hashingService = basicHashingService)
                val repo = UserRepositoryImpl(database = database, hashingService = mockk())

                val act = { repo.update(user = currentUser, oldPassword = null, newPassword = "newPassword") }

                shouldThrow<IllegalArgumentException> { act() }
            }

            it("throws if the old password doesn't match") {
                val currentUser =
                    createUser(userPassword = "OLDPASSWORD", hashingService = basicHashingService)
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
            val currentUser =
                createUser(userPassword = "PASSWORD", hashingService = basicHashingService)
            val repo = UserRepositoryImpl(database = database, hashingService = mockk())

            val deleted = repo.delete(currentUser.id)

            deleted.shouldBeTrue()
        }

        describe("User table constraints") {
            it("throws if a user with the same username exists") {
                val firstUser =
                    createUser(userPassword = "PASSWORD", hashingService = basicHashingService)
                val secondUser = firstUser.copy(id = 0, name = "Mark")
                val repo = UserRepositoryImpl(database = database, hashingService = basicHashingService)

                val act = { repo.create(secondUser, "OTHERPASSWORD") }

                shouldThrow<SQLException> { act() }
            }
        }
    }
})
