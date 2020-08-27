package usecases.role

import errors.RoleNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Role
import ports.RoleRepository

internal class FindRoleTest : DescribeSpec({
    describe("Find role use case") {
        it("finds a role by code") {
            val expectedRole = Role(id = 2, name = "Admin", code = "ADMIN")
            val roleRepository = mockk<RoleRepository> {
                every { find("ADMIN") } returns expectedRole
            }
            val findRole = FindRole(roleRepository)

            val role = findRole(FindRole.Parameters("ADMIN"))

            role.shouldBe(expectedRole)
            verify(exactly = 1) { roleRepository.find("ADMIN") }
        }

        it("throws 'RoleNotFound' in case the role isn't found") {
            val roleRepository = mockk<RoleRepository> {
                every { find("OTHER") } returns null
            }
            val findRole = FindRole(roleRepository)

            val act = { findRole(FindRole.Parameters("OTHER")) }

            val roleNotFound = shouldThrow<RoleNotFound>(act)
            roleNotFound.message.shouldBe("Role with code 'OTHER' not found")
            verify(exactly = 1) { roleRepository.find("OTHER") }
        }
    }
})