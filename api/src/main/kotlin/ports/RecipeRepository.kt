package ports

import model.Recipe
import model.SearchResult
import model.parameters.SearchRecipeRequestBody

interface RecipeRepository {
    fun find(id: Int): Recipe?
    fun getAll(): List<Recipe>
    fun getAll(userId: Int): List<Recipe>
    fun count(): Long
    fun search(requestBody: SearchRecipeRequestBody): SearchResult<Recipe>
    fun create(recipe: Recipe): Int
    fun update(recipe: Recipe)
    fun delete(id: Int): Boolean
}
