package usecases.recipe

import model.Recipe
import ports.RecipeRepository

class UpdateRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(recipe: Recipe) {
        recipeRepository.update(recipe)
    }
}
