package usecases.recipe

import errors.RecipeNotFound
import ports.RecipeRepository

class DeleteRecipe(private val recipeRepository: RecipeRepository) {

    operator fun invoke(parameters: Parameters) {
        val (recipeId) = parameters

        val deleted = recipeRepository.delete(recipeId)
        if (!deleted) throw RecipeNotFound(recipeId)
    }

    data class Parameters(val recipeId: Int)
}
