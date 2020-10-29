package usecases.userroles

import errors.RoleNotFound
import errors.UserNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import ports.RoleRepository
import ports.UserRepository
import ports.UserRolesRepository

internal class AddRoleToUserTest : DescribeSpec({
    describe("Add role to user use case") {
        it("adds a role to an user") {
            val userId = 2
            val roleId = 5
            val userRolesRepository = mockk<UserRolesRepository> {
                every { addRoleToUser(userId, roleId) } just Runs
            }
            val userRepository = mockk<UserRepository> {
                every { find(userId) } returns mockk()
            }
            val roleRepository = mockk<RoleRepository> {
                every { find(roleId) } returns mockk()
            }
            val addRoleToUser = AddRoleToUser(
                userRolesRepository = userRolesRepository,
                userRepository = userRepository,
                roleRepository = roleRepository,
            )

            addRoleToUser(AddRoleToUser.Parameters(userId, roleId))

            verify(exactly = 1) {
                userRepository.find(userId)
                roleRepository.find(roleId)
            }
            verify(exactly = 1) { userRolesRepository.addRoleToUser(userId, roleId) }
        }

        it("throws 'UserNotFound' if the user doesn't exist") {
            val userId = 2
            val roleId = 5
            val userRolesRepository = mockk<UserRolesRepository> {
                every { addRoleToUser(userId, roleId) } just Runs
            }
            val userRepository = mockk<UserRepository> {
                every { find(userId) } returns null
            }
            val roleRepository = mockk<RoleRepository> {
                every { find(roleId) } returns mockk()
            }
            val addRoleToUser = AddRoleToUser(
                userRolesRepository = userRolesRepository,
                userRepository = userRepository,
                roleRepository = roleRepository,
            )

            val act = { addRoleToUser(AddRoleToUser.Parameters(userId, roleId)) }

            val userNotFound = shouldThrow<UserNotFound>(act)
            userNotFound.message.shouldBe(UserNotFound(id = userId).message)
            verify(exactly = 1) { userRepository.find(userId) }
            verify(exactly = 0) {
                roleRepository wasNot Called
                userRolesRepository wasNot Called
            }
        }

        it("throws 'RoleNotFound' if the role isn't found") {
            val userId = 2
            val roleId = 5
            val userRolesRepository = mockk<UserRolesRepository> {
                every { addRoleToUser(userId, roleId) } just Runs
            }
            val userRepository = mockk<UserRepository> {
                every { find(userId) } returns mockk()
            }
            val roleRepository = mockk<RoleRepository> {
                every { find(roleId) } returns null
            }
            val addRoleToUser = AddRoleToUser(
                userRolesRepository = userRolesRepository,
                userRepository = userRepository,
                roleRepository = roleRepository,
            )

            val act = { addRoleToUser(AddRoleToUser.Parameters(userId, roleId)) }

            val roleNotFound = shouldThrow<RoleNotFound>(act)
            roleNotFound.message.shouldBe(RoleNotFound(id = roleId).message)
            verify(exactly = 1) {
                userRepository.find(userId)
                roleRepository.find(roleId)
            }
            verify {
                userRolesRepository wasNot Called
            }
        }
    }
})
