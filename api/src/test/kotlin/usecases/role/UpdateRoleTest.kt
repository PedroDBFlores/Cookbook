package usecases.role

import errors.OperationNotAllowed
import errors.RoleNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import model.Role
import ports.RoleRepository

internal class UpdateRoleTest : DescribeSpec({
    describe("Update role use case") {
        val basicRole = Role(id = 9, name = "Updated role", code = "NEWCODE", persistent = false)

        it("updates a role") {
            val roleRepository = mockk<RoleRepository>() {
                every { find(basicRole.id) } returns basicRole.copy(name = "Original role", code = "OLDCODE")
                every { update(basicRole) } just Runs
            }
            val updateRole = UpdateRole(roleRepository)

            updateRole(UpdateRole.Parameters(basicRole))

            verify(exactly = 1) {
                roleRepository.find(basicRole.id)
                roleRepository.update(basicRole)
            }
        }

        it("should throw 'RoleNotFound' if the role doesn't exist") {
            val roleRepository = mockk<RoleRepository>() {
                every { find(basicRole.id) } returns null
            }
            val updateRole = UpdateRole(roleRepository)

            val act = { updateRole(UpdateRole.Parameters(basicRole)) }

            val roleNotFound = shouldThrow<RoleNotFound>(act)
            roleNotFound.message.shouldBe("Role with id '${basicRole.id}' not found")
            verify(exactly = 1) { roleRepository.find(basicRole.id) }
            verify(exactly = 0) { roleRepository.update(basicRole) }
        }

        it("should throw 'OperationNotAllowed' if the found role is persistent") {
            val roleRepository = mockk<RoleRepository>() {
                every { find(basicRole.id) } returns basicRole.copy(
                    name = "Original role",
                    code = "OLDCODE",
                    persistent = true
                )
            }
            val updateRole = UpdateRole(roleRepository)

            val act = { updateRole(UpdateRole.Parameters(basicRole)) }

            val roleNotFound = shouldThrow<OperationNotAllowed>(act)
            roleNotFound.message.shouldBe("Cannot update a persistent role")
            verify(exactly = 1) { roleRepository.find(basicRole.id) }
            verify(exactly = 0) { roleRepository.update(basicRole) }
        }
    }
})