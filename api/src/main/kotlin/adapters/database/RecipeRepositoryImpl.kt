package adapters.database

import adapters.database.schema.*
import model.Recipe
import model.SearchResult
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ports.RecipeRepository
import kotlin.math.ceil

class RecipeRepositoryImpl(private val database: Database) : RecipeRepository {

    override fun find(id: Int): Recipe? = transaction(database) {
        RecipeEntity.findById(id)?.let(::mapToRecipe)
    }

    override fun getAll(): List<Recipe> = transaction(database) {
        RecipeEntity.all().map(::mapToRecipe)
    }

    override fun getAll(userId: Int): List<Recipe> = transaction(database) {
        RecipeEntity.find { Recipes.user eq userId }.map(::mapToRecipe)
    }

    override fun count(): Long = transaction(database) {
        RecipeEntity.count()
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
                (Recipes.recipeType eq RecipeTypes.id)
                    .and((Recipes.user eq Users.id))
            }

        if (name?.isNotEmpty() == true) {
            query.andWhere { Recipes.name like name }
        }

        if (description?.isNotEmpty() == true) {
            query.andWhere { Recipes.description like description }
        }

        val count = query.count()
        itemsPerPage.let { itemsPerPage ->
            val offset = pageNumber.toLong() * itemsPerPage
            query.limit(
                n = itemsPerPage,
                offset = offset
            )
        }

        val numberOfPages = ceil(count.toDouble() / itemsPerPage).toInt()
        val results = RecipeEntity.wrapRows(query).map(::mapToRecipe)
        SearchResult(count = count, numberOfPages = numberOfPages, results = results)
    }

    override fun create(recipe: Recipe): Int = transaction(database) {
        RecipeEntity.new {
            recipeType = RecipeTypeEntity.findById(recipe.recipeTypeId)!!
            user = UserEntity.findById(recipe.userId)!!
            name = recipe.name
            description = recipe.description
            ingredients = recipe.ingredients
            preparingSteps = recipe.preparingSteps
        }.id.value
    }

    override fun update(recipe: Recipe): Unit = transaction(database) {
        RecipeEntity.findById(recipe.id)?.let {
            it.recipeType = RecipeTypeEntity.findById(recipe.recipeTypeId)!!
            it.name = recipe.name
            it.description = recipe.description
            it.ingredients = recipe.ingredients
            it.preparingSteps = recipe.preparingSteps
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        RecipeEntity.findById(id)?.let {
            it.delete()
            true
        } ?: false
    }

    private fun mapToRecipe(entity: RecipeEntity) = Recipe(
        id = entity.id.value,
        recipeTypeId = entity.recipeType.id.value,
        recipeTypeName = entity.recipeType.name,
        userId = entity.user.id.value,
        userName = entity.user.name,
        name = entity.name,
        description = entity.description,
        ingredients = entity.ingredients,
        preparingSteps = entity.preparingSteps
    )
}
