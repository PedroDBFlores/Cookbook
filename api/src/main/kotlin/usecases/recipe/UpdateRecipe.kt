package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeRepository

class UpdateRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters) {
        val (recipe) = parameters

        require(recipeRepository.find(recipe.id) != null) {
            throw RecipeNotFound(recipeId = recipe.id)
        }

        recipeRepository.update(recipe)
    }

    data class Parameters(val recipe: Recipe)
}
