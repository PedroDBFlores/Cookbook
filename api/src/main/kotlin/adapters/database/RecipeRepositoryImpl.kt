package adapters.database

import adapters.database.schema.RecipeTypes
import adapters.database.schema.Recipes
import adapters.database.schema.Users
import model.Recipe
import model.SearchResult
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeRepository
import kotlin.math.ceil

class RecipeRepositoryImpl(private val database: Database) : RecipeRepository {

    override fun find(id: Int): Recipe? = transaction(database) {
        (Recipes innerJoin RecipeTypes innerJoin Users)
            .select {
                (Recipes.recipeTypeId eq RecipeTypes.id)
                    .and((Recipes.userId eq Users.id))
                    .and(Recipes.id eq id)
            }
            .mapNotNull(::mapToRecipe)
            .firstOrNull()
    }

    override fun getAll(): List<Recipe> = transaction(database) {
        (Recipes innerJoin RecipeTypes innerJoin Users).selectAll().map(::mapToRecipe)
    }

    override fun getAll(userId: Int): List<Recipe> = transaction(database) {
        (Recipes innerJoin RecipeTypes innerJoin Users).select { Recipes.userId eq userId }
            .map(::mapToRecipe)
    }

    override fun count(): Long = transaction(database) {
        Recipes.selectAll().count()
    }

    override fun search(
        name: String?,
        description: String?,
        recipeTypeId: Int?,
        pageNumber: Int,
        itemsPerPage: Int
    ): SearchResult<Recipe> = transaction(database) {
        val query = (Recipes innerJoin RecipeTypes innerJoin Users)
            .select {
                (Recipes.recipeTypeId eq RecipeTypes.id)
                    .and((Recipes.userId eq Users.id))
            }

        name?.let { nameParam ->
            query.andWhere { Recipes.name like nameParam }
        }
        description?.let { descriptionParam ->
            query.andWhere { Recipes.description like descriptionParam }
        }

        val count = query.count()
        itemsPerPage.let { itemsPerPage ->
            val offset = pageNumber.toLong().minus(1) * itemsPerPage
            query.limit(
                n = itemsPerPage,
                offset = offset
            )
        }

        val numberOfPages = ceil(count.toDouble() / itemsPerPage).toInt()
        val results = query.map(::mapToRecipe)
        SearchResult(count = count, numberOfPages = numberOfPages, results = results)
    }

    override fun create(recipe: Recipe): Int = transaction(database) {
        Recipes.insertAndGetId { recipeToCreate ->
            recipeToCreate[recipeTypeId] = recipe.recipeTypeId
            recipeToCreate[userId] = recipe.userId
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
        recipeTypeName = row[RecipeTypes.name],
        userId = row[Recipes.userId],
        userName = row[Users.name],
        name = row[Recipes.name],
        description = row[Recipes.description],
        ingredients = row[Recipes.ingredients],
        preparingSteps = row[Recipes.preparingSteps]
    )
}
