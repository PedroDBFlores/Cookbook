package usecases.recipetype

import errors.RecipeTypeAlreadyExists
import model.RecipeType
import ports.RecipeTypeRepository

class CreateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(parameters: Parameters): Int {
        val (recipeType) = parameters

        require(recipeTypeRepository.find(recipeType.name) == null) {
            throw RecipeTypeAlreadyExists(recipeType.name)
        }
        return recipeTypeRepository.create(recipeType)
    }

    data class Parameters(val recipeType: RecipeType)
}
