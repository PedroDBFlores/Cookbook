package adapters.database

import adapters.database.schema.RecipeTypeEntity
import adapters.database.schema.RecipeTypes
import model.RecipeType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeTypeRepository

class ExposedRecipeTypeRepository(database: Database) : ExposedRepository(database), RecipeTypeRepository {
    override fun find(id: Int): RecipeType? = transaction(database) {
        RecipeTypeEntity.findById(id)?.run(::mapToRecipeType)
    }

    override fun find(name: String): RecipeType? = transaction(database) {
        RecipeTypeEntity.find { RecipeTypes.name eq name }
            .map(::mapToRecipeType)
            .firstOrNull()
    }

    override fun getAll(): List<RecipeType> = transaction(database) {
        RecipeTypeEntity.all().map(::mapToRecipeType)
    }

    override fun count(): Long = transaction(database) {
        RecipeTypeEntity.count()
    }

    override fun create(recipeType: RecipeType): Int = transaction(database) {
        RecipeTypeEntity.new {
            name = recipeType.name
        }.id.value
    }

    override fun update(recipeType: RecipeType): Unit = transaction(database) {
        RecipeTypeEntity.findById(recipeType.id)?.run { name = recipeType.name }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        RecipeTypeEntity.findById(id)?.run {
            delete()
            true
        } ?: false
    }

    private fun mapToRecipeType(entity: RecipeTypeEntity) = RecipeType(
        id = entity.id.value,
        name = entity.name
    )
}
