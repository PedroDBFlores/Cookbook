package adapters.database.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Roles : IntIdTable() {
    val name = varchar("name", 64)
    val code = varchar("code", 16).uniqueIndex()
    val persistent = bool("persistent")
}

class RoleEntity (id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<RoleEntity>(Roles)
    var name by Roles.name
    var code by Roles.code
    var persistent by Roles.persistent
}