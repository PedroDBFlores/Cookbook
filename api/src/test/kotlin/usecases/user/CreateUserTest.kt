package usecases.user

import errors.UserAlreadyExists
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.User
import ports.UserRepository

internal class CreateUserTest : DescribeSpec({
    describe("Create user use case") {
        val basicUser = User(id = 0, name = "New user", userName = "NewUserName")

        it("creates a new user") {
            val userRepository = mockk<UserRepository> {
                every { find(ofType<String>()) } returns null
                every { create(user = basicUser, userPassword = "PASSWORD") } returns 1
            }
            val createUser = CreateUser(userRepository = userRepository)

            val id = createUser.invoke(CreateUser.Parameters(user = basicUser, userPassword = "PASSWORD"))

            id.shouldBe(1)
            verify {
                userRepository.find("NewUserName")
                userRepository.create(user = basicUser, userPassword = "PASSWORD")
            }

        }

        it("throws 'UserAlreadyExists' when a user with the same username already exists") {
            val user = basicUser.copy(userName = "dup")
            val userRepository = mockk<UserRepository> {
                every { find(user.userName) } returns user
            }

            val createUser = CreateUser(userRepository = userRepository)
            val act = { createUser.invoke(CreateUser.Parameters(user = user, userPassword = "PASSWORD")) }

            val userAlreadyExists = shouldThrow<UserAlreadyExists> { act() }
            userAlreadyExists.message.shouldBe("An user with the username 'dup' already exists")
            verify(exactly = 1) { userRepository.find("dup") }
            verify(exactly = 0) { userRepository.create(any(), any()) }
        }
    }
})
