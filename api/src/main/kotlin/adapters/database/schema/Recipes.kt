package adapters.database.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Recipes : IntIdTable() {
    val recipeType = reference("recipetypeid", RecipeTypes)
    val user = reference("userid", Users)
    val name: Column<String> = varchar("name", 128)
    val description: Column<String> = varchar("description", 256)
    val ingredients: Column<String> = varchar("ingredients", 2048)
    val preparingSteps: Column<String> = varchar("preparingsteps", 4096)
}

class RecipeEntity (id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<RecipeEntity>(Recipes)
    var recipeType by RecipeTypeEntity referencedOn Recipes.recipeType
    var user by UserEntity referencedOn Recipes.user
    var name by Recipes.name
    var description by Recipes.description
    var ingredients by Recipes.ingredients
    var preparingSteps by Recipes.preparingSteps
}
