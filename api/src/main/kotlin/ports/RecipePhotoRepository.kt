package ports

import model.Recipe
import model.RecipePhoto

interface RecipePhotoRepository {
    fun find(recipe: Recipe): Array<RecipePhoto>
    fun create(recipePhoto: RecipePhoto): Int
}