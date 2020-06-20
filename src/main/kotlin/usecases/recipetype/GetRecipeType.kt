package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeRepository

internal class GetRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {

    operator fun invoke(parameters: Parameters): RecipeType {
        return recipeTypeRepository.get(parameters.recipeTypeId) ?: throw RecipeTypeNotFound(parameters.recipeTypeId)
    }

    data class Parameters(val recipeTypeId: Int)
}