package ports

import usecases.recipe.*
import usecases.recipetype.*

internal interface RecipeTypeDependencies {
    val getRecipeType: GetRecipeType
    val getAllRecipeTypes: GetAllRecipeTypes
    val createRecipeType: CreateRecipeType
    val updateRecipeType: UpdateRecipeType
    val deleteRecipeType: DeleteRecipeType
}

internal interface RecipeDependencies {
    val getRecipe: GetRecipe
    val getAllRecipes: GetAllRecipes
    val createRecipe: CreateRecipe
    val updateRecipe: UpdateRecipe
    val deleteRecipe: DeleteRecipe
}