package usecases.recipetype

import errors.RecipeTypeNotFound
import ports.RecipeTypeDeleter

class DeleteRecipeType(private val recipeTypeDeleter: RecipeTypeDeleter) {

    suspend operator fun invoke(parameters: Parameters) {
        if (!recipeTypeDeleter(parameters.recipeTypeId)) throw RecipeTypeNotFound(parameters.recipeTypeId)
    }

    data class Parameters(val recipeTypeId: Int)
}
