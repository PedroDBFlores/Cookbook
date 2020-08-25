package usecases.recipetype

import errors.RecipeTypeNotFound
import ports.RecipeTypeRepository

class DeleteRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {

    operator fun invoke(parameters: Parameters) {
        val (recipeTypeId) = parameters

        val deleted = recipeTypeRepository.delete(recipeTypeId)
        if (!deleted) throw RecipeTypeNotFound(recipeTypeId)
    }

    data class Parameters(val recipeTypeId: Int)
}
