package ports

import model.RecipePhoto

interface RecipePhotoRepository {
    fun find(id: Int): RecipePhoto?
    fun getAll(recipeId: Int): List<RecipePhoto>
    fun create(recipePhoto: RecipePhoto): Int
    fun delete(id: Int): Boolean
    fun deleteAll(recipeId: Int): Boolean
}
