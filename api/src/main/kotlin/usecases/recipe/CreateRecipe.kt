package usecases.recipe

import model.Recipe
import ports.RecipeRepository

class CreateRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(recipe: Recipe): Int {
        return recipeRepository.create(recipe)
    }
}