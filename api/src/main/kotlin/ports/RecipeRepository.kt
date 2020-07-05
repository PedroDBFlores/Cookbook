package ports

import model.Recipe
import model.SearchResult
import model.parameters.SearchRecipeParameters

interface RecipeRepository {
    fun find(id: Int): Recipe?
    fun getAll(): List<Recipe>
    fun count(): Long
    fun search(parameters: SearchRecipeParameters): SearchResult<Recipe>
    fun create(recipe: Recipe): Int
    fun update(recipe: Recipe)
    fun delete(id: Int): Boolean
}
