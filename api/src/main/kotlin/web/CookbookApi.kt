package web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.plugin.Plugin
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import ports.RecipeDependencies
import ports.RecipeTypeDependencies
import web.recipe.*
import web.recipe.CreateRecipeHandler
import web.recipe.DeleteRecipeHandler
import web.recipe.GetAllRecipesHandler
import web.recipe.GetRecipeHandler
import web.recipe.UpdateRecipeHandler
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
        .exception(BadRequestResponse::class.java) { ex, ctx ->
            handleError(ex, ctx)
        }
        .exception(Exception::class.java) { ex, ctx ->
            handleError(ex, ctx)
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
                    put(UpdateRecipeHandler(recipeDependencies.updateRecipe))
                    delete("/:id", DeleteRecipeHandler(recipeDependencies.deleteRecipe))
                }
            }
        }.after(::enableStrictTransportSecurity)


    //region Methods
    fun start() {
        app.start(port)
    }

    override fun close() {
        app.stop()
    }

    private fun enableStrictTransportSecurity(context: Context) {
        if (context.header("X-Forwarded-Proto") == "https") {
            context.header("Strict-Transport-Security", "max-age=31536000")
        }
    }

    private fun handleError(ex: Exception, ctx: Context) {
        println("${ex.javaClass}\n${ex.message}")
        when (ex) {
            is BadRequestResponse -> ctx.status(HttpStatus.BAD_REQUEST_400).error(
                code = "BAD_REQUEST",
                message = ex.message.toString()
            )
            else -> ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500).error(
                code = "INTERNAL_SERVER_ERROR",
                message = "Unexpected error (${ex.javaClass.simpleName}): ${ex.message}"
            )
        }
    }
    //endregion Methods
}





