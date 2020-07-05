package usecases.recipetype

import model.RecipeType
import ports.RecipeTypeRepository

class UpdateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {

    operator fun invoke(recipeType: RecipeType) {
        recipeTypeRepository.update(recipeType)
    }
}
