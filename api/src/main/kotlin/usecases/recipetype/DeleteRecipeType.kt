package usecases.recipetype

import errors.RecipeTypeNotFound
import ports.RecipeTypeRepository

class DeleteRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {

    operator fun invoke(parameters: Parameters) {
        if (!recipeTypeRepository.delete(parameters.recipeTypeId)) throw RecipeTypeNotFound(parameters.recipeTypeId)
    }

    data class Parameters(val recipeTypeId: Int)
}
