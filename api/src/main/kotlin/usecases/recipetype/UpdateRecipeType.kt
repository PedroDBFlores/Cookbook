package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeFinderById
import ports.RecipeTypeUpdater

class UpdateRecipeType(
    private val recipeTypeFinderById: RecipeTypeFinderById,
    private val recipeTypeUpdater: RecipeTypeUpdater
) {
    suspend operator fun invoke(parameters: Parameters): Unit = with(parameters) {
        recipeTypeFinderById(id)?.let {
            recipeTypeUpdater(RecipeType(id, name))
        } ?: throw RecipeTypeNotFound(id = id)
    }

    data class Parameters(val id: Int, val name: String)
}
