package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeRepository

class FindRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): Recipe {
        val (recipeId) = parameters

        return recipeRepository.find(recipeId) ?: throw RecipeNotFound(recipeId)
    }

    data class Parameters(val recipeId: Int)
}
