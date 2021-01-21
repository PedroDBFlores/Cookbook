package usecases.recipe

import errors.RecipeNotFound
import ports.RecipeRepository

class DeleteRecipe(private val recipeRepository: RecipeRepository) {

    operator fun invoke(parameters: Parameters) {
        if (!recipeRepository.delete(parameters.recipeId)) throw RecipeNotFound(parameters.recipeId)
    }

    data class Parameters(val recipeId: Int)
}
