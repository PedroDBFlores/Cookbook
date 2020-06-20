package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeRepository

internal class UpdateRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(recipe: Recipe) {
        recipeRepository.get(recipe.id) ?: throw RecipeNotFound(recipe.id)
        recipeRepository.update(recipe)
    }
}