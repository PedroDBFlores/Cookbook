package adapters.database

import adapters.database.schema.RecipeTypes
import model.RecipeType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeTypeRepository

class RecipeTypeRepositoryImpl(private val database: Database) : RecipeTypeRepository {
    override fun find(id: Int): RecipeType? = transaction(database) {
        RecipeTypes.select { RecipeTypes.id eq id }
            .mapNotNull(::mapToRecipeType)
            .firstOrNull()
    }

    override fun find(name: String): RecipeType? = transaction(database) {
        RecipeTypes.select { RecipeTypes.name eq name }
            .mapNotNull(::mapToRecipeType)
            .firstOrNull()
    }

    override fun getAll(): List<RecipeType> = transaction(database) {
        RecipeTypes.selectAll().map(::mapToRecipeType)
    }

    override fun count(): Long = transaction(database) {
        RecipeTypes.selectAll().count()
    }

    override fun create(recipeType: RecipeType): Int = transaction(database) {
        RecipeTypes.insertAndGetId { recipeTypeToCreate ->
            recipeTypeToCreate[name] = recipeType.name
        }.value
    }

    override fun update(recipeType: RecipeType): Unit = transaction(database) {
        RecipeTypes.update({ RecipeTypes.id eq recipeType.id }) { recipeTypeToUpdate ->
            recipeTypeToUpdate[name] = recipeType.name
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        RecipeTypes.deleteWhere { RecipeTypes.id eq id } > 0
    }

    private fun mapToRecipeType(row: ResultRow) = RecipeType(
        id = row[RecipeTypes.id].value,
        name = row[RecipeTypes.name]
    )
}
