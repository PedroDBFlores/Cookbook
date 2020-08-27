package usecases.user

import errors.UserNotFound
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.UserRepository

internal class DeleteUserTest : DescribeSpec({
    describe("Delete user use case") {
        it("deletes an user") {
            val userRepository = mockk<UserRepository> {
                every { delete(1) } returns true
            }
            val deleteUser = DeleteUser(userRepository)

            deleteUser.invoke(DeleteUser.Parameters(userId = 1))

            verify(exactly = 1) { userRepository.delete(1) }
        }

        it("throws 'UserNotFound if no row is affected") {
            val userRepository = mockk<UserRepository> {
                every { delete(any()) } returns false
            }
            val deleteUser = DeleteUser(userRepository)

            val act = { deleteUser.invoke(DeleteUser.Parameters(userId = 1)) }

            val userNotFound = shouldThrow<UserNotFound>(act)
            userNotFound.message.shouldBe(UserNotFound(id = 1).message)
            verify(exactly = 1) { userRepository.delete(1) }
        }
    }
})
