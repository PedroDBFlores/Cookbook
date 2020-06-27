package ports

import model.Recipe

interface RecipeRepository {
    fun find(id: Int): Recipe?
    fun getAll(): List<Recipe>
    fun count(): Long
    fun create(recipe: Recipe): Int
    fun update(recipe: Recipe)
    fun delete(id: Int): Boolean
}
