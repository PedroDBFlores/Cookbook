package adapters.database.schema

import model.RecipeType
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object RecipeTypes : IntIdTable() {
    val name: Column<String> = varchar("name", 64).uniqueIndex()
}

class RecipeTypeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RecipeTypeEntity>(RecipeTypes)
    var name by RecipeTypes.name

    val recipes by RecipeEntity referrersOn Recipes.recipeType

    fun mapToRecipeType() = RecipeType(
        id = this.id.value,
        name = this.name
    )
}
