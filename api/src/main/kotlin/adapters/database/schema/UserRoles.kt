package adapters.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable

object UserRoles : IntIdTable() {
    val userId = integer("userid").references(Users.id)
    val roleId = integer("roleid").references(Roles.id)
}
