package usecases.recipetype

import errors.RecipeTypeAlreadyExists
import model.RecipeType
import ports.RecipeTypeCreator
import ports.RecipeTypeFinderByName

class CreateRecipeType(
    private val recipeTypeFinderByName: RecipeTypeFinderByName,
    private val recipeTypeCreator: RecipeTypeCreator
) {

    suspend operator fun invoke(parameters: Parameters): Int = with(parameters) {
        recipeTypeFinderByName(name)?.let {
            throw RecipeTypeAlreadyExists(name)
        } ?: recipeTypeCreator(RecipeType(name = name))
    }

    data class Parameters(val name: String)
}
