package adapters.database

import adapters.database.DatabaseTestHelper.createRole
import adapters.database.schema.Roles
import config.Dependencies
import errors.RoleNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DTOGenerator
import java.sql.SQLException

class RoleRepositoryImplTest : DescribeSpec({
    val database = Dependencies.database

    beforeSpec {
        transaction(database) {
            SchemaUtils.create(Roles)
        }
    }

    afterTest {
        transaction(database) {
            Roles.deleteAll()
        }
    }
    
    describe("Role repository") {
        it("finds a role by it's id") {
            val createdRole = createRole(database = database)
            val repo = RoleRepositoryImpl(database = database)

            val role = repo.find(createdRole.id)

            role.shouldNotBeNull()
            role.shouldBe(createdRole)
        }

        it("finds a role by it's code") {
            val createdRole = createRole(database = database)
            val repo = RoleRepositoryImpl(database = database)

            val role = repo.find(createdRole.code)

            role.shouldNotBeNull()
            role.shouldBe(createdRole)
        }

        it("gets all the roles") {
            val createdRoles = arrayOf(
                createRole(database = database),
                createRole(database = database)
            )
            val repo = RoleRepositoryImpl(database = database)

            val allRoles = repo.getAll()

            allRoles.shouldBe(createdRoles)
        }

        describe("create") {
            it("creates a new role") {
                val roleToCreate = DTOGenerator.generateRole(id = 0)
                val repo = RoleRepositoryImpl(database = database)

                val id = repo.create(role = roleToCreate)

                id.shouldNotBeZero()
            }
        }

        describe("update") {
            it("updates a role") {
                val createdRole = createRole(database = database)
                val repo = RoleRepositoryImpl(database = database)

                repo.update(createdRole.copy(name = "UPDATEDROLE"))
            }

            it("throws if the role doesn't exist") {
                val createdRole = createRole(database = database)
                val repo = RoleRepositoryImpl(database = database)

                val act = { repo.update(createdRole.copy(code = "NEWCODE")) }

                shouldThrow<RoleNotFound> { act() }
            }
        }

        it("deletes a role") {
            val createdRole = createRole(database = database)
            val repo = RoleRepositoryImpl(database = database)

            val deleted = repo.delete(createdRole.id)

            deleted.shouldBeTrue()
        }

        describe("Role table constraints") {
            it("it throws if a duplicate role code is inserted") {
                val createdRole = createRole(database = database)
                val duplicateRole = createdRole.copy(id = 0, name = "ABC")
                val repo = RoleRepositoryImpl(database = database)

                val act = { repo.create(role = duplicateRole) }

                shouldThrow<SQLException> { act() }
            }
        }
    }
})