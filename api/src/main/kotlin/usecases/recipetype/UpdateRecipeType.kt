package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeRepository

class UpdateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(parameters: Parameters) {
        val (recipeType) = parameters

        require(recipeTypeRepository.find(recipeType.id) != null) {
            throw RecipeTypeNotFound(recipeTypeId = recipeType.id)
        }

        recipeTypeRepository.update(recipeType)
    }

    data class Parameters(val recipeType: RecipeType)
}
