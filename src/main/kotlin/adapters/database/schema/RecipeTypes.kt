package adapters.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object RecipeTypes : IntIdTable() {
    val name: Column<String> = varchar("name", 64).uniqueIndex()
}