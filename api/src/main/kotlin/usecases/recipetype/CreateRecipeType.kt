package usecases.recipetype

import errors.RecipeTypeAlreadyExists
import model.RecipeType
import ports.RecipeTypeRepository

class CreateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {

    operator fun invoke(parameters: Parameters): Int = with(parameters) {
        recipeTypeRepository.find(name)?.let {
            throw RecipeTypeAlreadyExists(name)
        } ?: recipeTypeRepository.create(RecipeType(name = name))
    }

    data class Parameters(val name: String)
}
