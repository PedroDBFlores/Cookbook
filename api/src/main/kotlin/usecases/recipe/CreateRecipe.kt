package usecases.recipe

import model.Recipe
import ports.RecipeCreator

class CreateRecipe(
    private val recipeCreator: RecipeCreator,
) {
    suspend operator fun invoke(parameters: Parameters): Int =
        recipeCreator(parameters.toRecipe())

    data class Parameters(
        val recipeTypeId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String,
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
