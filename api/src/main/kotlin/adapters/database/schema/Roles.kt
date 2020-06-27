package adapters.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable

object Roles : IntIdTable() {
    val name = varchar("name", 64)
    val code = varchar("code", 16).uniqueIndex()
    val persistent = bool("persistent")
}
