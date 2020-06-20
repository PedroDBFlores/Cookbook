package adapters.database

import adapters.database.schema.Recipes
import model.Recipe
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeRepository

class RecipeRepositoryImpl(private val database: Database) : RecipeRepository {

    override fun get(id: Int): Recipe? = transaction(database) {
        Recipes.select { Recipes.id eq id }
            .mapNotNull { resultRow -> mapToRecipe(resultRow) }
            .firstOrNull()
    }

    override fun getAll(): List<Recipe> = transaction(database) {
        Recipes.selectAll()
            .map { resultRow -> mapToRecipe(resultRow) }
    }

    override fun create(recipe: Recipe): Int = transaction(database) {
        Recipes.insertAndGetId {
            it[recipeTypeId] = recipe.recipeTypeId
            it[name] = recipe.name
            it[description] = recipe.description
            it[ingredients] = recipe.ingredients
            it[preparingSteps] = recipe.preparingSteps
        }.value
    }

    override fun update(recipe: Recipe): Unit = transaction(database) {
        Recipes.update({ Recipes.id eq recipe.id }) {
            it[recipeTypeId] = recipe.recipeTypeId
            it[name] = recipe.name
            it[description] = recipe.description
            it[ingredients] = recipe.ingredients
            it[preparingSteps] = recipe.preparingSteps
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        Recipes.deleteWhere { Recipes.id eq id } > 0
    }

    override fun mapToRecipe(row: ResultRow): Recipe {
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