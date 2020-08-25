package usecases.user

import errors.UserNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.User
import ports.UserRepository

internal class FindUserTest : DescribeSpec({
    describe("Find user use case") {
        it("returns a user") {
            val expectedUser =  User(id = 1, name = "New user", userName = "NewUserName")
            val userRepository = mockk<UserRepository> {
                every { find(expectedUser.id) } returns expectedUser
            }
            val findUser = FindUser(userRepository)

            val user = findUser(FindUser.Parameters(userId = expectedUser.id))

            user.shouldBe(expectedUser)
            verify(exactly = 1) { userRepository.find(expectedUser.id) }
        }

        it("throws if the user is not found") {
            val userRepository = mockk<UserRepository> {
                every { find(ofType<Int>()) } returns null
            }
            val findUser = FindUser(userRepository)

            val act = { findUser(FindUser.Parameters(userId = 1)) }

            shouldThrow<UserNotFound> { act() }
        }
    }
})
