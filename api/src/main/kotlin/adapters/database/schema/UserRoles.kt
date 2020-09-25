package adapters.database.schema

import org.jetbrains.exposed.sql.Table

object UserRoles : Table() {
    val userId = reference("userid", Users)
    val roleId = reference("roleid", Roles)
    override val primaryKey = PrimaryKey(userId, roleId, name = "PK_UserRoles_userId_roleId")
}