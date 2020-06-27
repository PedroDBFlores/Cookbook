package ports

import usecases.recipe.*
import usecases.recipetype.*

interface RecipeTypeDependencies {
    val findRecipeType: FindRecipeType
    val getAllRecipeTypes: GetAllRecipeTypes
    val createRecipeType: CreateRecipeType
    val updateRecipeType: UpdateRecipeType
    val deleteRecipeType: DeleteRecipeType
}

interface RecipeDependencies {
    val findRecipe: FindRecipe
    val getAllRecipes: GetAllRecipes
    val createRecipe: CreateRecipe
    val updateRecipe: UpdateRecipe
    val deleteRecipe: DeleteRecipe
}