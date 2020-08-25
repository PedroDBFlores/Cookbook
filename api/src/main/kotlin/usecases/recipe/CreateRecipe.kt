package usecases.recipe

import model.Recipe
import ports.RecipeRepository

class CreateRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): Int {
        val (recipe) = parameters

        return recipeRepository.create(recipe)
    }

    data class Parameters(val recipe: Recipe)
}
