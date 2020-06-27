package adapters.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    val name: Column<String> = varchar("name", 128)
    val userName: Column<String> = varchar("username", 255).uniqueIndex()
    val passwordHash: Column<String> = varchar("passwordHash", 255)
}