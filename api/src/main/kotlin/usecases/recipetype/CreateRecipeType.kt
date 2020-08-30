package usecases.recipetype

import errors.RecipeTypeAlreadyExists
import model.RecipeType
import ports.RecipeTypeRepository

class CreateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(parameters: Parameters): Int {
        val (name) = parameters

        require(recipeTypeRepository.find(name) == null) {
            throw RecipeTypeAlreadyExists(name)
        }
        return recipeTypeRepository.create(RecipeType(name = name))
    }

    data class Parameters(val name: String)
}
