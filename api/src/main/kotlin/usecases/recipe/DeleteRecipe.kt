package usecases.recipe

import errors.RecipeNotFound
import ports.RecipeDeleter

class DeleteRecipe(private val recipeDeleter: RecipeDeleter) {

    suspend operator fun invoke(parameters: Parameters) {
        if (!recipeDeleter(parameters.recipeId)) throw RecipeNotFound(parameters.recipeId)
    }

    data class Parameters(val recipeId: Int)
}
