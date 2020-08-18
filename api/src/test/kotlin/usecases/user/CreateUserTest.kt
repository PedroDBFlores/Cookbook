package usecases.user

import errors.UserAlreadyExists
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.UserRepository
import utils.DTOGenerator

internal class CreateUserTest : DescribeSpec({
    describe("Create user use case") {
        it("creates a new user") {
            val userToCreate = DTOGenerator.generateUser(id = 0)
            val userRepository = mockk<UserRepository> {
                every { find(ofType<String>()) } returns null
                every { create(user = userToCreate, userPassword = "PASSWORD") } returns 1
            }
            val createUser = CreateUser(userRepository = userRepository)

            val id = createUser.invoke(user = userToCreate, userPassword = "PASSWORD")

            id.shouldBe(1)
            verify(exactly = 1) { userRepository.create(user = userToCreate, userPassword = "PASSWORD") }
        }

        arrayOf(
            row(DTOGenerator.generateUser(id = 0, username = "already"), "a user with the same username already exists")
        ).forEach { (user, description) ->
            it("throws 'UserAlreadyExists' when $description") {
                val userRepository = mockk<UserRepository> {
                    every { find(user.username) } returns user
                }

                val createUser = CreateUser(userRepository = userRepository)
                val act = { createUser.invoke(user = user, userPassword = "PASSWORD") }

                shouldThrow<UserAlreadyExists> { act() }
            }
        }
    }
})
