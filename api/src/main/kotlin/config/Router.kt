package config

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.path
import org.koin.core.KoinComponent
import web.recipe.*
import web.recipetype.*

class Router(
    private val recipeTypeDependencies: RecipeTypeDependencies,
    private val recipeDependencies: RecipeDependencies
) : KoinComponent {
    fun register(app: Javalin) {
        app.routes {
            path("/api") {
                path("/recipetype") {
                    ApiBuilder.get("/:id", FindRecipeTypeHandler(recipeTypeDependencies.findRecipeType))
                    ApiBuilder.get(GetAllRecipeTypesHandler(recipeTypeDependencies.getAllRecipeTypes))
                    ApiBuilder.post(CreateRecipeTypeHandler(recipeTypeDependencies.createRecipeType))
                    ApiBuilder.put(UpdateRecipeTypeHandler(recipeTypeDependencies.updateRecipeType))
                    ApiBuilder.delete("/:id", DeleteRecipeTypeHandler(recipeTypeDependencies.deleteRecipeType))
                }
                path("/recipe") {
                    ApiBuilder.get("/:id", FindRecipeHandler(recipeDependencies.findRecipe))
                    ApiBuilder.get(GetAllRecipesHandler(recipeDependencies.getAllRecipes))
                    ApiBuilder.post(CreateRecipeHandler(recipeDependencies.createRecipe))
                    ApiBuilder.put(UpdateRecipeHandler(recipeDependencies.updateRecipe))
                    ApiBuilder.delete("/:id", DeleteRecipeHandler(recipeDependencies.deleteRecipe))
                }
            }
        }
    }
}
