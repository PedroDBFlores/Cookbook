package usecases.userroles

import errors.UserRoleNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.UserRolesRepository

internal class DeleteRoleFromUserTest : DescribeSpec({
    describe("Delete role from user use case") {
        it("deletes a role from an user") {
            val userId = 9
            val roleId = 1
            val userRolesRepository = mockk<UserRolesRepository> {
                every { deleteRoleFromUser(userId, roleId) } returns true
            }
            val deleteRoleFromUser = DeleteRoleFromUser(userRolesRepository)

            deleteRoleFromUser(DeleteRoleFromUser.Parameters(userId, roleId))

            verify { userRolesRepository.deleteRoleFromUser(userId, roleId) }
        }

        it("throws 'UserRoleNotFound' when no rows are affected"){
            val userId = 1
            val roleId = 7
            val userRolesRepository = mockk<UserRolesRepository> {
                every { deleteRoleFromUser(userId, roleId) } returns false
            }
            val deleteRoleFromUser = DeleteRoleFromUser(userRolesRepository)

            val act = { deleteRoleFromUser(DeleteRoleFromUser.Parameters(userId, roleId))}

            val userRoleNotFound = shouldThrow<UserRoleNotFound>(act)
            userRoleNotFound.message.shouldBe(UserRoleNotFound(userId, roleId).message)
            verify { userRolesRepository.deleteRoleFromUser(userId, roleId) }
        }
    }
})