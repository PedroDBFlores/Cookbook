package usecases.recipetype

import errors.RecipeTypeNotFound
import model.RecipeType
import ports.RecipeTypeRepository

internal class UpdateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {

    operator fun invoke(recipeType: RecipeType) {
        recipeTypeRepository.get(recipeType.id) ?: throw RecipeTypeNotFound(recipeType.id)
        recipeTypeRepository.update(recipeType)
    }
}