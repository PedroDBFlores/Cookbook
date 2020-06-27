package adapters.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Recipes : IntIdTable() {
    val recipeTypeId: Column<Int> = integer("recipeTypeId").references(RecipeTypes.id)
    val name: Column<String> = varchar("name", 128).uniqueIndex()
    val description: Column<String> = varchar("description", 256)
    val ingredients: Column<String> = varchar("ingredients", 2048)
    val preparingSteps: Column<String> = varchar("preparingSteps", 4096)
}