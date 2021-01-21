package usecases.recipe

import errors.RecipeNotFound
import model.Recipe
import ports.RecipeRepository

class UpdateRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters) {
        check(recipeRepository.find(parameters.id) != null) {
            throw RecipeNotFound(id = parameters.id)
        }

        recipeRepository.update(parameters.toRecipe())
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
