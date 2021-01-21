package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeRepository

class UpdateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(parameters: Parameters) {
        val (id, name) = parameters

        check(recipeTypeRepository.find(id) != null) {
            throw RecipeTypeNotFound(id = id)
        }

        recipeTypeRepository.update(RecipeType(id, name))
    }

    data class Parameters(val id: Int, val name: String)
}
