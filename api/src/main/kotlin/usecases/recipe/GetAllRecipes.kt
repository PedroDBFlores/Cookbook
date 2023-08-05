package usecases.recipe

import model.Recipe
import ports.RecipeLister

class GetAllRecipes(private val recipeLister: RecipeLister) {
    suspend operator fun invoke(): List<Recipe> = recipeLister()
}
