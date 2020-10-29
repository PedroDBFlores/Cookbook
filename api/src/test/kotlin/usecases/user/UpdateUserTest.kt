package usecases.user

import errors.UserNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import model.User
import ports.UserRepository

class UpdateUserTest : DescribeSpec({
    describe("Update user use case") {
        it("updates only the user's name in the database") {
            val currentUser = User(id = 1, name = "Emanuel", userName = "emanuelj")
            val newName = "Gabriel"
            val userRepository = mockk<UserRepository> {
                every { find(currentUser.id) } returns currentUser
                every { update(currentUser.copy(name = newName)) } just Runs
            }
            val updateUser = UpdateUser(userRepository)

            updateUser(UpdateUser.Parameters(currentUser.id, newName))

            verify(exactly = 1) {
                userRepository.find(currentUser.id)
                userRepository.update(currentUser.copy(name = newName), null, null)
            }
        }

        it("updates the user's password in the database") {
            val currentUser = User(id = 1, name = "Emanuel", userName = "emanuelj")
            val oldPass = "oldPass"
            val newPass = "newPass"
            val userRepository = mockk<UserRepository> {
                every { find(currentUser.id) } returns currentUser
                every { update(currentUser, oldPass, newPass) } just Runs
            }
            val updateUser = UpdateUser(userRepository)

            updateUser(UpdateUser.Parameters(currentUser.id, "Emanuel", oldPass, newPass))

            verify(exactly = 1) {
                userRepository.find(currentUser.id)
                userRepository.update(currentUser, oldPass, newPass)
            }
        }

        it("throws 'UserNotFound' if the user doesn't exist") {
            val userRepository = mockk<UserRepository> {
                every { find(ofType<Int>()) } returns null
            }
            val updateUser = UpdateUser(userRepository)

            val act = { updateUser(UpdateUser.Parameters(1, "newName")) }

            val userNotFound = shouldThrow<UserNotFound>(act)
            userNotFound.message.shouldBe(UserNotFound(id = 1).message)
            verify(exactly = 1) { userRepository.find(ofType<Int>()) }
            verify(exactly = 0) { userRepository.update(any()) }
        }
    }
})
