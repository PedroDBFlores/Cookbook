package usecases.role

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import model.Role
import ports.RoleRepository

class GetAllRolesTest : DescribeSpec({
    describe("Search Roles use case") {
        it("returns all roles") {
            val expectedRoles = listOf(
                Role(id = 1, name = "X", code = "X"),
                Role(id = 2, name = "Y", code = "Y", persistent = true)
            )
            val roleRepository = mockk<RoleRepository> {
                every { getAll() } returns expectedRoles
            }
            val getAllRoles = GetAllRoles(roleRepository)

            val roles = getAllRoles()
            roles.shouldBe(expectedRoles)
        }
    }
})