package adapters.database

import adapters.database.schema.Recipes
import model.Recipe
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeRepository

class RecipeRepositoryImpl(private val database: Database) : RecipeRepository {

    override fun find(id: Int): Recipe? = transaction(database) {
        Recipes.select { Recipes.id eq id }
            .mapNotNull { row -> mapToRecipe(row) }
            .firstOrNull()
    }

    override fun getAll(): List<Recipe> = transaction(database) {
        Recipes.selectAll()
            .map { row -> mapToRecipe(row) }
    }

    override fun count(): Long = transaction(database) {
        Recipes.selectAll().count()
    }

    override fun create(recipe: Recipe): Int = transaction(database) {
        Recipes.insertAndGetId { recipeToCreate ->
            recipeToCreate[recipeTypeId] = recipe.recipeTypeId
            recipeToCreate[name] = recipe.name
            recipeToCreate[description] = recipe.description
            recipeToCreate[ingredients] = recipe.ingredients
            recipeToCreate[preparingSteps] = recipe.preparingSteps
        }.value
    }

    override fun update(recipe: Recipe): Unit = transaction(database) {
        Recipes.update({ Recipes.id eq recipe.id }) { recipeToUpdate ->
            recipeToUpdate[recipeTypeId] = recipe.recipeTypeId
            recipeToUpdate[name] = recipe.name
            recipeToUpdate[description] = recipe.description
            recipeToUpdate[ingredients] = recipe.ingredients
            recipeToUpdate[preparingSteps] = recipe.preparingSteps
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        Recipes.deleteWhere { Recipes.id eq id } > 0
    }

    private fun mapToRecipe(row: ResultRow) = Recipe(
        id = row[Recipes.id].value,
        recipeTypeId = row[Recipes.recipeTypeId],
        name = row[Recipes.name],
        description = row[Recipes.description],
        ingredients = row[Recipes.ingredients],
        preparingSteps = row[Recipes.preparingSteps]
    )
}