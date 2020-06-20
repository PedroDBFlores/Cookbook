package web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.plugin.Plugin
import org.eclipse.jetty.http.HttpStatus
import ports.RecipeDependencies
import ports.RecipeTypeDependencies
import web.recipe.CreateRecipeHandler
import web.recipe.GetAllRecipesHandler
import web.recipe.GetRecipeHandler
import web.recipetype.*

/**
 * Defines the Cookbook API
 */
internal class CookbookApi(
    private val port: Int = 8080,
    private val recipeTypeDependencies: RecipeTypeDependencies,
    private val recipeDependencies: RecipeDependencies,
    private val plugins: List<Plugin>
) : AutoCloseable {

    private val app: Javalin = Javalin.create { config ->
        plugins.forEach { config.registerPlugin(it) }
    }
        .exception(Exception::class.java) { exception, ctx ->
            println("${exception.javaClass}\n${exception.message}")
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                .error(
                    "INTERNAL_SERVER_ERROR",
                    "Unexpected error (${exception.javaClass.simpleName}): ${exception.message}"
                )
        }
        .routes {
            path("/api") {
                path("/recipetype") {
                    get("/:id", GetRecipeTypeHandler(recipeTypeDependencies.getRecipeType))
                    get(GetAllRecipeTypesHandler(recipeTypeDependencies.getAllRecipeTypes))
                    post(CreateRecipeTypeHandler(recipeTypeDependencies.createRecipeType))
                    put(UpdateRecipeTypeHandler(recipeTypeDependencies.updateRecipeType))
                    delete("/:id", DeleteRecipeTypeHandler(recipeTypeDependencies.deleteRecipeType))
                }
                path("/recipe") {
                    get("/:id", GetRecipeHandler(recipeDependencies.getRecipe))
                    get(GetAllRecipesHandler(recipeDependencies.getAllRecipes))
                    post(CreateRecipeHandler(recipeDependencies.createRecipe))
                }
            }
        }


    //region Methods
    fun start() {
        app.start(port)
    }

    override fun close() {
        app.stop()
    }


    //endregion Methods
}





