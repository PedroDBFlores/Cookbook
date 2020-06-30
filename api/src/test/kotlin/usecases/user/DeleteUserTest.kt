package usecases.user

import errors.UserNotFound
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import ports.UserRepository

internal class DeleteUserTest : DescribeSpec({
    describe("Delete user use case") {
        it("deletes an user") {
            val userRepository = mockk<UserRepository> {
                every { delete(1) } returns true
            }
            val deleteUser = DeleteUser(userRepository)

            val act = { deleteUser.invoke(DeleteUser.Parameters(userId = 1)) }

            shouldNotThrowAny { act() }
        }

        it("throws if a user isn't deleted") {
            val userRepository = mockk<UserRepository> {
                every { delete(any()) } returns false
            }
            val deleteUser = DeleteUser(userRepository)

            val act = { deleteUser.invoke(DeleteUser.Parameters(userId = 1)) }

            shouldThrow<UserNotFound> { act() }
        }
    }
})