package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeRepository

class FindRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(parameters: Parameters): RecipeType =
        recipeTypeRepository.find(parameters.recipeTypeId) ?: throw RecipeTypeNotFound(parameters.recipeTypeId)

    data class Parameters(val recipeTypeId: Int)
}
