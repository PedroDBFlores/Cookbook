package usecases.recipetype

import model.RecipeType
import ports.RecipeTypeRepository

class CreateRecipeType(private val recipeTypeRepository: RecipeTypeRepository) {
    operator fun invoke(recipeType: RecipeType): Int {
        return recipeTypeRepository.create(recipeType)
    }
}