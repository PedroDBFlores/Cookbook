package usecases.role

import errors.RoleAlreadyExists
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Role
import ports.RoleRepository

internal class CreateRoleTest : DescribeSpec({
    describe("Create role use case") {
        it("creates a role") {
            val roleToCreate = Role(name = "New Role", code = "NEWROLE")
            val roleRepository = mockk<RoleRepository> {
                every { find(ofType<String>()) } returns null
                every { create(roleToCreate) } returns 1
            }
            val createRole = CreateRole(roleRepository = roleRepository)

            val id = createRole(CreateRole.Parameters("New Role", "NEWROLE"))

            id.shouldBe(1)
            verify(exactly = 1) { roleRepository.find("NEWROLE") }
            verify(exactly = 1) { roleRepository.create(roleToCreate) }
        }

        it("throws if a role with the same code already exists") {
            val roleRepository = mockk<RoleRepository> {
                every { find("OLDROLE") } returns Role(name = "Old role", code = "OLDROLE")
            }
            val createRole = CreateRole(roleRepository = roleRepository)

            val act = { createRole(CreateRole.Parameters("Old Role", "OLDROLE")) }

            val roleAlreadyExists = shouldThrow<RoleAlreadyExists> (act)
            roleAlreadyExists.message.shouldBe("A role with the code OLDROLE already exists")
            verify(exactly = 1) { roleRepository.find("OLDROLE") }
            verify(exactly = 0) { roleRepository.create(any()) }
        }
    }
})