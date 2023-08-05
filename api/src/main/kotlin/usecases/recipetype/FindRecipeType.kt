package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeFinderById

class FindRecipeType(private val recipeTypeFinderById: RecipeTypeFinderById) {
    suspend operator fun invoke(parameters: Parameters): RecipeType =
        recipeTypeFinderById(parameters.recipeTypeId) ?: throw RecipeTypeNotFound(parameters.recipeTypeId)

    data class Parameters(val recipeTypeId: Int)
}
