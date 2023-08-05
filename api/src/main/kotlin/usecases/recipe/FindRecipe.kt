package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeFinder

class FindRecipe(private val recipeFinder: RecipeFinder) {
    suspend operator fun invoke(parameters: Parameters): Recipe =
        recipeFinder(parameters.recipeId) ?: throw RecipeNotFound(parameters.recipeId)

    data class Parameters(val recipeId: Int)
}
