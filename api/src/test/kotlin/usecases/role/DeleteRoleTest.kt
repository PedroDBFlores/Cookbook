package usecases.role

import errors.OperationNotAllowed
import errors.RoleNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Role
import ports.RoleRepository

internal class DeleteRoleTest : DescribeSpec({
    describe("Delete role use case") {
        it("deletes a role") {
            val roleId = 7
            val roleRepository = mockk<RoleRepository> {
                every { find(roleId) } returns Role(id = 7, name = "ROLE", code = "CODE", persistent = false)
                every { delete(roleId) } returns true
            }
            val deleteRole = DeleteRole(roleRepository)

            deleteRole(DeleteRole.Parameters(roleId))

            verify {
                roleRepository.find(roleId)
                roleRepository.delete(roleId)
            }
        }

        it("throws 'RoleNotFound' if the role doesn't exist") {
            val roleId = 987
            val roleRepository = mockk<RoleRepository> {
                every { find(roleId) } returns null
            }
            val deleteRole = DeleteRole(roleRepository)

            val act = { deleteRole(DeleteRole.Parameters(roleId)) }

            val roleNotFound = shouldThrow<RoleNotFound>(act)
            roleNotFound.message.shouldBe(RoleNotFound(id = roleId).message)
            verify(exactly = 1) { roleRepository.find(roleId) }
            verify(exactly = 0) { roleRepository.delete(roleId) }
        }

        it("throws 'OperationNotAllowed' when trying to delete a role that's persistent") {
            val roleId = 2
            val roleRepository = mockk<RoleRepository> {
                every { find(roleId) } returns Role(id = 1, name = "XPTO", code = "XPTO", persistent = true)
            }

            val deleteRole = DeleteRole(roleRepository)

            val act = { deleteRole(DeleteRole.Parameters(roleId)) }

            val roleNotFound = shouldThrow<OperationNotAllowed>(act)
            roleNotFound.message.shouldBe("Cannot delete a persistent role")
            verify(exactly = 1) { roleRepository.find(roleId) }
            verify(exactly = 0) { roleRepository.delete(roleId) }
        }
    }
})