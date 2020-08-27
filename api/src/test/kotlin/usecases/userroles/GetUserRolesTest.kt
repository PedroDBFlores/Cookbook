package usecases.userroles

import errors.UserNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import model.UserRole
import ports.UserRepository
import ports.UserRolesRepository

internal class GetUserRolesTest : DescribeSpec({
    describe("Get user roles use case") {
        it("get's the user's roles") {
            val userId = 1
            val expectedRoles = listOf(
                UserRole(1, 2),
                UserRole(1, 4)
            )
            val userRolesRepository = mockk<UserRolesRepository> {
                every { getRolesForUser(userId) } returns expectedRoles
            }
            val userRepository = mockk<UserRepository> {
                every { find(userId) } returns mockk()
            }
            val getUserRoles = GetUserRoles(
                userRolesRepository = userRolesRepository,
                userRepository = userRepository
            )

            val userRoles = getUserRoles(GetUserRoles.Parameters(userId))

            userRoles.shouldBe(expectedRoles)
            verify(exactly = 1) {
                userRepository.find(userId)
                userRolesRepository.getRolesForUser(userId)
            }
        }

        it("throws 'UserNotFound' if the user doesn't exist") {
            val userId = 1

            val userRolesRepository = mockk<UserRolesRepository> {
                every { getRolesForUser(userId) } returns mockk()
            }
            val userRepository = mockk<UserRepository> {
                every { find(userId) } returns null
            }
            val getUserRoles = GetUserRoles(
                userRolesRepository = userRolesRepository,
                userRepository = userRepository
            )

            val act = { getUserRoles(GetUserRoles.Parameters(userId)) }

            val userNotFound = shouldThrow<UserNotFound>(act)
            userNotFound.message.shouldBe(UserNotFound(id = userId).message)
            verify(exactly = 1) { userRepository.find(userId) }
            verify { userRolesRepository wasNot Called }
        }
    }
})