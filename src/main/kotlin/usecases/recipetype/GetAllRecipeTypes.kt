package usecases.recipetype

import model.RecipeType
import ports.RecipeTypeRepository

internal class GetAllRecipeTypes(private val recipeTypeRepository: RecipeTypeRepository) {

    operator fun invoke(): List<RecipeType> {
        return recipeTypeRepository.getAll()
    }
}