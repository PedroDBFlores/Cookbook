package ports

import model.RecipeType

interface RecipeTypeRepository {
    fun find(id: Int): RecipeType?
    fun getAll(): List<RecipeType>
    fun count(): Long
    fun create(recipeType: RecipeType): Int
    fun update(recipeType: RecipeType)
    fun delete(id: Int): Boolean
}