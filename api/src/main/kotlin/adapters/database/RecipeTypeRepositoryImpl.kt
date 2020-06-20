package adapters.database

import org.jetbrains.exposed.sql.*
import adapters.database.schema.RecipeTypes
import ports.RecipeTypeRepository
import model.RecipeType
import org.jetbrains.exposed.sql.transactions.transaction

internal class RecipeTypeRepositoryImpl(private val database: Database) : RecipeTypeRepository {
    override fun get(id: Int): RecipeType? = transaction(database) {
        RecipeTypes.select { RecipeTypes.id eq id }
            .mapNotNull { resultRow -> mapToRecipeType(resultRow) }
            .firstOrNull()
    }

    override fun getAll(): List<RecipeType> = transaction(database) {
        RecipeTypes.selectAll()
            .map { resultRow -> mapToRecipeType(resultRow) }
    }

    override fun create(recipeType: RecipeType): Int = transaction(database) {
        RecipeTypes.insertAndGetId {
            it[name] = recipeType.name
        }.value
    }

    override fun update(recipeType: RecipeType): Unit = transaction(database) {
        RecipeTypes.update({ RecipeTypes.id eq recipeType.id }) {
            it[name] = recipeType.name
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        RecipeTypes.deleteWhere { RecipeTypes.id eq id } > 0
    }

    override fun mapToRecipeType(row: ResultRow): RecipeType {
        return RecipeType(
            id = row[RecipeTypes.id].value,
            name = row[RecipeTypes.name]
        )
    }
}