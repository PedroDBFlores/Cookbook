package adapters.database

import adapters.database.schema.Roles
import errors.RoleNotFound
import model.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RoleRepository

class RoleRepositoryImpl(private val database: Database) : RoleRepository {
    override fun find(id: Int): Role? = transaction(database) {
        Roles.select { Roles.id eq id }.mapNotNull(::mapToRole)
            .firstOrNull()
    }

    override fun find(code: String): Role? = transaction(database) {
        Roles.select { Roles.code eq code }.mapNotNull(::mapToRole)
            .firstOrNull()
    }

    override fun getAll(): List<Role> = transaction(database) {
        Roles.selectAll().map(::mapToRole)
    }

    override fun create(role: Role): Int = transaction(database) {
        Roles.insertAndGetId { roleToInsert ->
            roleToInsert[name] = role.name
            roleToInsert[code] = role.code
            roleToInsert[persistent] = role.persistent
        }.value
    }

    override fun update(role: Role): Unit = transaction(database) {
        Roles.update({ Roles.id eq role.id }) { roleToUpdate ->
            roleToUpdate[name] = role.name
            roleToUpdate[code] = role.code
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        Roles.deleteWhere { Roles.id eq id } > 0
    }

    private fun mapToRole(row: ResultRow) = Role(
        id = row[Roles.id].value,
        name = row[Roles.name],
        code = row[Roles.code],
        persistent = row[Roles.persistent]
    )
}
