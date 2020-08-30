package ports

import model.Recipe
import model.SearchResult

interface RecipeRepository {
    fun find(id: Int): Recipe?
    fun getAll(): List<Recipe>
    fun getAll(userId: Int): List<Recipe>
    fun count(): Long
    fun search(
        name: String?,
        description: String?,
        recipeTypeId: Int?,
        pageNumber: Int,
        itemsPerPage: Int
    ): SearchResult<Recipe>

    fun create(recipe: Recipe): Int
    fun update(recipe: Recipe)
    fun delete(id: Int): Boolean
}
