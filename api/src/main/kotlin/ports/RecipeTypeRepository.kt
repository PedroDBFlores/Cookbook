package ports

import model.RecipeType
import org.jetbrains.exposed.sql.ResultRow

internal interface RecipeTypeRepository {
    fun get(id: Int): RecipeType?
    fun getAll(): List<RecipeType>
    fun create(recipeType: RecipeType): Int
    fun update(recipeType: RecipeType)
    fun delete(id: Int) : Boolean
    fun mapToRecipeType(row: ResultRow): RecipeType
}