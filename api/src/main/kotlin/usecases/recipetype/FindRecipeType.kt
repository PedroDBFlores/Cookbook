package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeRepository

class FindRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(parameters: Parameters): RecipeType {
        val (recipeTypeId) = parameters

        return recipeTypeRepository.find(recipeTypeId) ?: throw RecipeTypeNotFound(recipeTypeId)
    }

    data class Parameters(val recipeTypeId: Int)
}
