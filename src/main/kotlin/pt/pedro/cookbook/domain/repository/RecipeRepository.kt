package pt.pedro.cookbook.domain.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import pt.pedro.cookbook.domain.DatabaseManager
import pt.pedro.cookbook.domain.table.Recipes
import pt.pedro.cookbook.dto.Recipe

internal class RecipeRepository : CrudRepository<Recipe> {
    override suspend fun get(id: Int): Recipe? {
        return DatabaseManager.returnFromTransaction {
            Recipes.select {
                Recipes.id eq id
            }
                .mapNotNull { resultRow -> mapToResult(resultRow) }
                .firstOrNull()
        }
    }

    override suspend fun getAll(): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun create(entity: Recipe): Recipe {
        return DatabaseManager.returnFromTransaction {
            val id = Recipes.insertAndGetId {
                it[recipeTypeId] = entity.recipeTypeId
                it[name] = entity.name
                it[description] = entity.description
                it[ingredients] = entity.ingredients
                it[preparingSteps] = entity.preparingSteps
            }
            return@returnFromTransaction entity.copy(id = id.value)
        }
    }

    override suspend fun update(entity: Recipe): Recipe {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun mapToResult(row: ResultRow): Recipe {
        return Recipe(
            id = row[Recipes.id].value,
            recipeTypeId = row[Recipes.recipeTypeId],
            name = row[Recipes.name],
            description = row[Recipes.description],
            ingredients = row[Recipes.ingredients],
            preparingSteps = row[Recipes.preparingSteps]
        )
    }
}