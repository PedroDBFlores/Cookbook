package adapters.database

import adapters.database.DatabaseTestHelper.createRoleInDatabase
import adapters.database.DatabaseTestHelper.createUserInDatabase
import adapters.database.DatabaseTestHelper.mapToUserRole
import adapters.database.schema.Roles
import adapters.database.schema.UserRoles
import adapters.database.schema.Users
import errors.UserNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import model.Role
import model.User
import model.UserRole
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

internal class UserRolesRepositoryImplTest : DescribeSpec({
    val database = DatabaseTestHelper.database

    beforeTest {
        transaction(database) {
            UserRoles.deleteAll()
            Roles.deleteAll()
            Users.deleteAll()
        }
    }

    describe("User roles repository") {
        val basicUser = User(id = 0, name = "Fábio Silva", userName = "fab.silva")
        val firstRole = Role(id = 0, name = "User", code = "USER", persistent = true)
        val secondRole = Role(id = 0, name = "Admin", code = "ADMIN", persistent = true)

        describe("Get roles") {
            it("gets all the roles for a user") {
                val user = createUserInDatabase(basicUser, "password", mockk(relaxed = true))
                val createdRoles = arrayOf(
                    createRoleInDatabase(firstRole),
                    createRoleInDatabase(secondRole)
                )

                val expectedUserRoles = createdRoles.map { role ->
                    UserRole(userId = user.id, roleId = role.id)
                }
                val repo = UserRolesRepositoryImpl(database = database)
                createdRoles.forEach { role ->
                    repo.addRoleToUser(userId = user.id, roleId = role.id)
                }

                val userRoles = repo.getRolesForUser(user.id)

                userRoles.shouldBe(expectedUserRoles)
            }

            it("should throw UserNotFound if the user doesn't exist") {
                val repo = UserRolesRepositoryImpl(database = database)

                val act = { repo.getRolesForUser(88) }

                shouldThrow<UserNotFound> { act() }
            }
        }

        describe("Add role to user") {
            it("adds a role to a user") {
                val user = createUserInDatabase(basicUser, "password", mockk(relaxed = true))
                val role = createRoleInDatabase(firstRole)
                val repo = UserRolesRepositoryImpl(database = database)

                repo.addRoleToUser(userId = user.id, roleId = role.id)

                val createdUserRole = transaction(database) {
                    UserRoles.select { UserRoles.userId eq user.id }.map { row -> row.mapToUserRole() }
                        .first()
                }
                createdUserRole.shouldBe(UserRole(userId = user.id, roleId = role.id ))
            }

            arrayOf(
                row(
                    { createUserInDatabase(basicUser, "password", mockk(relaxed = true)) },
                    { null },
                    "when there's no matching role"
                ), row({ null }, { createRoleInDatabase(secondRole) }, "when there's no matching user")
            ).forEach { (createUser: () -> User?, createRole: () -> Role?, description) ->
                it("should throw $description") {
                    val user = createUser()
                    val role = createRole()
                    val repo = UserRolesRepositoryImpl(database = database)

                    val act = { repo.addRoleToUser(userId = user?.id ?: 777, roleId = role?.id ?: 888) }

                    shouldThrow<SQLException> { act() }
                }
            }
        }

        describe("Delete role from user") {
            it("deletes a role from a user") {
                val user = createUserInDatabase(basicUser, "password", mockk(relaxed = true))
                val role = createRoleInDatabase(secondRole)
                val repo = UserRolesRepositoryImpl(database = database)
                repo.addRoleToUser(userId = user.id, roleId = role.id)

                val deleted = repo.deleteRoleFromUser(userId = user.id, roleId = role.id)

                deleted.shouldBe(true)
            }
        }
    }
})
