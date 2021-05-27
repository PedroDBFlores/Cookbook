package adapters.database.schema

import model.RecipePhoto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RecipePhotos : IntIdTable() {
    val recipe = reference("recipeid", Recipes)
    val name = varchar("name", 128)
    val data = blob("data")
}

class RecipePhotoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RecipePhotoEntity>(RecipePhotos)

    var recipe by RecipeEntity referencedOn RecipePhotos.recipe
    var name by RecipePhotos.name
    var data by RecipePhotos.data

    fun toRecipePhoto() = RecipePhoto(
        id = this.id.value,
        recipeId = this.recipe.id.value,
        name = this.name,
        data = this.data.bytes
    )
}
