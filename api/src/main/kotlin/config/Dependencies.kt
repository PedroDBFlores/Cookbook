package config

import adapters.authentication.JWTProvider
import io.javalin.core.plugin.Plugin
import io.javalin.core.security.AccessManager
import model.User
import usecases.recipe.*
import usecases.recipetype.*

data class CookbookApiDependencies(
    val configurationFile: ConfigurationFile,
    val plugins: List<Plugin>,
    val router: Router
)

data class JWTDependencies(
    val jwtProvider: JWTProvider<User>,
    val accessManager: AccessManager
)

data class RecipeTypeDependencies(
    val findRecipeType: FindRecipeType,
    val getAllRecipeTypes: GetAllRecipeTypes,
    val createRecipeType: CreateRecipeType,
    val updateRecipeType: UpdateRecipeType,
    val deleteRecipeType: DeleteRecipeType
)

data class RecipeDependencies(
    val findRecipe: FindRecipe,
    val getAllRecipes: GetAllRecipes,
    val createRecipe: CreateRecipe,
    val updateRecipe: UpdateRecipe,
    val deleteRecipe: DeleteRecipe
)
