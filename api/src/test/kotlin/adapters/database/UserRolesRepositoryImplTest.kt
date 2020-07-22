package adapters.database

import adapters.database.DatabaseTestHelper.mapToUserRole
import adapters.database.schema.Roles
import adapters.database.schema.UserRoles
import adapters.database.schema.Users
import errors.UserNotFound
import errors.UserRoleNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import model.Role
import model.User
import model.UserRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DTOGenerator
import java.sql.SQLException

internal class UserRolesRepositoryImplTest : DescribeSpec({
    val database = DatabaseTestHelper.database

    beforeSpec {
        transaction(database) {
            SchemaUtils.create(Users, Roles, UserRoles)
        }
    }

    afterTest {
        transaction(database) {
            UserRoles.deleteAll()
            Roles.deleteAll()
            Users.deleteAll()
        }
    }

    fun createUser(): User {
        val user = DTOGenerator.generateUser(id = 0)
        val id = transaction(database) {
            Users.insertAndGetId { createUser ->
                createUser[name] = user.name
                createUser[userName] = user.username
                createUser[passwordHash] = user.passwordHash
            }
        }.value
        return user.copy(id = id)
    }

    fun createRole(): Role {
        val role = DTOGenerator.generateRole(id = 0)
        val id = transaction {
            Roles.insertAndGetId { roleToInsert ->
                roleToInsert[name] = role.name
                roleToInsert[code] = role.code
                roleToInsert[persistent] = role.persistent
            }
        }.value
        return role.copy(id = id)
    }

    describe("User roles repository") {
        describe("Get roles") {
            it("gets all the roles for a user") {
                val user = createUser()
                val createdRoles = arrayOf(createRole(), createRole())
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
                val user = createUser()
                val role = createRole()
                val repo = UserRolesRepositoryImpl(database = database)

                repo.addRoleToUser(userId = user.id, roleId = role.id)

                val createdUserRole = transaction(database) {
                    UserRoles.select { UserRoles.userId eq user.id }.map { row -> row.mapToUserRole() }
                        .first()
                }
                createdUserRole.shouldBe(UserRole(userId = user.id, roleId = role.id))
            }

            arrayOf(
                row(createUser(), null, "when there's no matching role"),
                row(null, createRole(), "when there's no matching user")
            ).forEach { (user, role, description) ->
                it("should throw $description") {
                    val repo = UserRolesRepositoryImpl(database = database)

                    val act = { repo.addRoleToUser(userId = user?.id ?: 777, roleId = role?.id ?: 888) }

                    shouldThrow<SQLException> { act() }
                }
            }
        }

        describe("Delete role from user") {
            it("deletes a role from a user") {
                val user = createUser()
                val role = createRole()
                val repo = UserRolesRepositoryImpl(database = database)
                repo.addRoleToUser(userId = user.id, roleId = role.id)

                val deleted = repo.deleteRoleFromUser(userId = user.id, roleId = role.id)

                deleted.shouldBe(true)
            }

            arrayOf(
                row(null, 999, "when the role doesn't exist"),
                row(999, null, "when the user doesn't exist")
            ).forEach { (userId, roleId, description) ->
                it("throws $description") {
                    val user = createUser()
                    val role = createRole()
                    val repo = UserRolesRepositoryImpl(database = database)
                    repo.addRoleToUser(userId = user.id, roleId = role.id)

                    val act = { repo.deleteRoleFromUser(userId ?: user.id, roleId ?: role.id) }

                    shouldThrow<UserRoleNotFound> { act() }
                }
            }
        }
    }
})
