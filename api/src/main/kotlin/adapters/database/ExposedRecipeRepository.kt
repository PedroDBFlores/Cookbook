package adapters.database

import adapters.database.schema.*
import model.Recipe
import model.SearchResult
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.ceil

class ExposedRecipeRepository(database: Database) : ExposedRepository(database) {

    fun find(id: Int): Recipe? = transaction(database) {
        RecipeEntity.findById(id)?.run(RecipeEntity::mapToRecipe)
    }

    fun getAll(): List<Recipe> = transaction(database) {
        RecipeEntity.all().map(RecipeEntity::mapToRecipe)
    }

    fun count(): Long = transaction(database) {
        RecipeEntity.count()
    }

    fun search(
        name: String?,
        description: String?,
        recipeTypeId: Int?,
        pageNumber: Int,
        itemsPerPage: Int
    ): SearchResult<Recipe> = transaction(database) {
        val query = (Recipes innerJoin RecipeTypes)
            .select {
                (Recipes.recipeType eq RecipeTypes.id)
            }

        if (name?.isNotEmpty() == true) {
            query.andWhere { Recipes.name like name }
        }

        if (description?.isNotEmpty() == true) {
            query.andWhere { Recipes.description like description }
        }

        val count = query.count()
        itemsPerPage.let { itemsPerPage ->
            val offset = (pageNumber.toLong() - 1) * itemsPerPage
            query.limit(
                n = itemsPerPage,
                offset = offset
            )
        }

        val numberOfPages = ceil(count.toDouble() / itemsPerPage).toInt()
        val results = RecipeEntity.wrapRows(query).map(RecipeEntity::mapToRecipe)
        SearchResult(count = count, numberOfPages = numberOfPages, results = results)
    }

    fun create(recipe: Recipe): Int = transaction(database) {
        RecipeEntity.new {
            recipeType = RecipeTypeEntity[recipe.recipeTypeId]
            name = recipe.name
            description = recipe.description
            ingredients = recipe.ingredients
            preparingSteps = recipe.preparingSteps
        }.id.value
    }

    fun update(recipe: Recipe): Unit = transaction(database) {
        RecipeEntity.findById(recipe.id)?.run {
            recipeType = RecipeTypeEntity[recipe.recipeTypeId]
            name = recipe.name
            description = recipe.description
            ingredients = recipe.ingredients
            preparingSteps = recipe.preparingSteps
        }
    }

    fun delete(id: Int): Boolean = transaction(database) {
        RecipeEntity.findById(id)?.run {
            delete()
            true
        } ?: false
    }
}
