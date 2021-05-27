package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeRepository

class UpdateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(parameters: Parameters): Unit = with(parameters) {
        recipeTypeRepository.find(id)?.let {
            recipeTypeRepository.update(RecipeType(id, name))
        } ?: throw RecipeTypeNotFound(id = id)
    }

    data class Parameters(val id: Int, val name: String)
}
