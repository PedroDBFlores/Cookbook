package adapters.database

import adapters.database.schema.RecipeEntity
import adapters.database.schema.RecipePhotoEntity
import adapters.database.schema.RecipePhotos
import model.Recipe
import model.RecipePhoto
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipePhotoRepository

class ExposedRecipePhotoRepository(private val database: Database) : RecipePhotoRepository {
    override fun find(recipe: Recipe): Array<RecipePhoto> {
        TODO("Not yet implemented")
    }

    override fun create(recipePhoto: RecipePhoto): Int = transaction(database) {
        RecipePhotoEntity.new {
            recipe = RecipeEntity[recipePhoto.recipeId]
            name = recipePhoto.name
            data = ExposedBlob(recipePhoto.data)
        }.id.value
    }
}
