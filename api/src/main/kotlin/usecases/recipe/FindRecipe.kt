package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeRepository

class FindRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): Recipe =
        recipeRepository.find(parameters.recipeId) ?: throw RecipeNotFound(parameters.recipeId)

    data class Parameters(val recipeId: Int)
}
