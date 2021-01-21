package usecases.recipe

import model.Recipe
import ports.RecipeRepository

class CreateRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): Int =
        recipeRepository.create(parameters.toRecipe())

    data class Parameters(
        val recipeTypeId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String
    ) {
        fun toRecipe() = Recipe(
            recipeTypeId = recipeTypeId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}
