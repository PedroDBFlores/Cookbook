package adapters.database

import adapters.database.schema.RecipeEntity
import adapters.database.schema.RecipePhotoEntity
import adapters.database.schema.RecipePhotos
import model.RecipePhoto
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipePhotoRepository

class ExposedRecipePhotoRepository(database: Database) : ExposedRepository(database), RecipePhotoRepository {
    override fun find(id: Int): RecipePhoto? = transaction(database) {
        RecipePhotoEntity.findById(id)?.run(::toRecipePhoto)
    }

    override fun getAll(recipeId: Int): List<RecipePhoto> = transaction(database) {
        RecipePhotoEntity.find { RecipePhotos.recipe eq recipeId }
            .map(::toRecipePhoto)
    }

    override fun create(recipePhoto: RecipePhoto): Int = transaction(database) {
        RecipePhotoEntity.new {
            recipe = RecipeEntity[recipePhoto.recipeId]
            name = recipePhoto.name
            data = ExposedBlob(recipePhoto.data)
        }.id.value
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        RecipePhotoEntity.findById(id)?.run {
            delete()
            true
        } ?: false
    }

    override fun deleteAll(recipeId: Int): Boolean = transaction(database) {
        RecipePhotoEntity.find { RecipePhotos.recipe eq recipeId }
            .takeUnless { x -> x.empty() }
            ?.forEach { it.delete() }?.run { true } ?: false
    }

    private fun toRecipePhoto(entity: RecipePhotoEntity) = RecipePhoto(
        id = entity.id.value,
        recipeId = entity.recipe.id.value,
        name = entity.name,
        data = entity.data.bytes
    )
}
