package adapters.database

import adapters.database.schema.RecipeTypeEntity
import adapters.database.schema.RecipeTypes
import model.RecipeType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedRecipeTypeRepository(database: Database) : ExposedRepository(database) {
    fun find(id: Int): RecipeType? = transaction(database) {
        RecipeTypeEntity.findById(id)?.run(RecipeTypeEntity::mapToRecipeType)
    }

    fun find(name: String): RecipeType? = transaction(database) {
        RecipeTypeEntity.find { RecipeTypes.name eq name }
            .map(RecipeTypeEntity::mapToRecipeType)
            .firstOrNull()
    }

    fun getAll(): List<RecipeType> = transaction(database) {
        RecipeTypeEntity.all().map(RecipeTypeEntity::mapToRecipeType).sortedBy { x -> x.id }
    }

    fun count(): Long = transaction(database) {
        RecipeTypeEntity.count()
    }

    fun create(recipeType: RecipeType): Int = transaction(database) {
        RecipeTypeEntity.new {
            name = recipeType.name
        }.id.value
    }

    fun update(recipeType: RecipeType): Unit = transaction(database) {
        RecipeTypeEntity.findById(recipeType.id)?.run { name = recipeType.name }
    }

    fun delete(id: Int): Boolean = transaction(database) {
        RecipeTypeEntity.findById(id)?.run {
            delete()
            true
        } ?: false
    }
}
