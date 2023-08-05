package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeFinder
import ports.RecipeUpdater

class UpdateRecipe(
    private val recipeFinder: RecipeFinder,
    private val recipeUpdater: RecipeUpdater
) {
    suspend operator fun invoke(parameters: Parameters) {
        check(recipeFinder(parameters.id) != null) {
            throw RecipeNotFound(id = parameters.id)
        }
        recipeUpdater(parameters.toRecipe())
    }

    data class Parameters(
        val id: Int,
        val recipeTypeId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String
    ) {
        fun toRecipe() = Recipe(
            id = id,
            recipeTypeId = recipeTypeId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}
