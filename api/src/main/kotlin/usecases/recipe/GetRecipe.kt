package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeRepository

internal class GetRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): Recipe {
        return recipeRepository.get(parameters.recipeId) ?: throw RecipeNotFound(parameters.recipeId)
    }

    data class Parameters(val recipeId: Int)
}