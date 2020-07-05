package adapters.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable

object UserRoles : IntIdTable() {
    val userId = integer("userId").references(Users.id)
    val roleId = integer("roleId").references(Roles.id)
}
