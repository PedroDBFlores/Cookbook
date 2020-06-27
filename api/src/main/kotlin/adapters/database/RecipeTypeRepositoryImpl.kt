package adapters.database

import adapters.database.schema.RecipeTypes
import errors.RecipeTypeNotFound
import model.RecipeType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeTypeRepository

class RecipeTypeRepositoryImpl(private val database: Database) : RecipeTypeRepository {
    override fun find(id: Int): RecipeType? = transaction(database) {
        RecipeTypes.select { RecipeTypes.id eq id }
            .mapNotNull { row -> mapToRecipeType(row) }
            .firstOrNull()
    }

    override fun getAll(): List<RecipeType> = transaction(database) {
        RecipeTypes.selectAll()
            .map { row -> mapToRecipeType(row) }
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
        val affectedRows = RecipeTypes.update({ RecipeTypes.id eq recipeType.id }) { recipeTypeToUpdate ->
            recipeTypeToUpdate[name] = recipeType.name
        }
        require(affectedRows == 1) { throw RecipeTypeNotFound(recipeTypeId = recipeType.id) }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        RecipeTypes.deleteWhere { RecipeTypes.id eq id } > 0
    }

    private fun mapToRecipeType(row: ResultRow) = RecipeType(
        id = row[RecipeTypes.id].value,
        name = row[RecipeTypes.name]
    )
}