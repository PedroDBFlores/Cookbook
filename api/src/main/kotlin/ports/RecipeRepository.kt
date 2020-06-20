package ports

import model.Recipe
import org.jetbrains.exposed.sql.ResultRow

internal interface RecipeRepository {
    fun get(id: Int): Recipe?
    fun getAll(): List<Recipe>
    fun create(recipe: Recipe): Int
    fun update(recipe: Recipe)
    fun delete(id: Int) : Boolean
    fun mapToRecipe(row: ResultRow): Recipe
}
