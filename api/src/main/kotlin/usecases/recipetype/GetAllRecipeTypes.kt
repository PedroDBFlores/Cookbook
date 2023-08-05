package usecases.recipetype

import model.RecipeType
import ports.RecipeTypeLister

class GetAllRecipeTypes(private val recipeTypeLister: RecipeTypeLister) {
    suspend operator fun invoke(): List<RecipeType> = recipeTypeLister.invoke()
}
