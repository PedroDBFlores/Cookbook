package adapters.database

import adapters.database.schema.RoleEntity
import adapters.database.schema.Roles
import model.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RoleRepository

class RoleRepositoryImpl(private val database: Database) : RoleRepository {
    override fun find(id: Int): Role? = transaction(database) {
        RoleEntity.findById(id)?.run(::mapToRole)
    }

    override fun find(code: String): Role? = transaction(database) {
        RoleEntity.find { Roles.code eq code }
            .map(::mapToRole)
            .firstOrNull()
    }

    override fun getAll(): List<Role> = transaction(database) {
        RoleEntity.all().map(::mapToRole)
    }

    override fun create(role: Role): Int = transaction(database) {
        RoleEntity.new {
            name = role.name
            code = role.code
            persistent = role.persistent
        }.id.value
    }

    override fun update(role: Role): Unit = transaction(database) {
        RoleEntity.findById(role.id)?.run {
            name = role.name
            code = role.code
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        RoleEntity.findById(id)?.run {
            delete()
            true
        } ?: false
    }

    private fun mapToRole(entity: RoleEntity) = Role(
        id = entity.id.value,
        name = entity.name,
        code = entity.code,
        persistent = entity.persistent
    )
}
