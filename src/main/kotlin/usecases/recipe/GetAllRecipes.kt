package usecases.recipe

import model.Recipe
import ports.RecipeRepository

internal class GetAllRecipes(private val recipeRepository: RecipeRepository) {
    operator fun invoke(): List<Recipe> = recipeRepository.getAll()
}