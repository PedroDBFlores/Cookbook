package web.recipe

import errors.RecipeNotFound
import errors.RecipeTypeNotFound
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.HttpMethod
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiParam
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.DeleteRecipe

internal class DeleteRecipeHandler(private val deleteRecipe: DeleteRecipe) : Handler {
    @OpenApi(
        summary = "Delete recipe",
        description = "Deletes a recipe by id",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam(name = "id", description = "Recipe type id")],
        responses = [OpenApiResponse(
            status = "204"
        ), OpenApiResponse(
            status = "404",
            description = "When the recipe type to delete wasn't found"
        )],
        tags = ["Recipe"]
    )
    override fun handle(ctx: Context) {
        try {
            val recipeId = ctx.pathParam("id", Int::class.java).get()
            deleteRecipe(DeleteRecipe.Parameters(recipeId))
            ctx.status(HttpStatus.NO_CONTENT_204)
        } catch (ex: RecipeNotFound) {
            ctx.status(HttpStatus.NOT_FOUND_404)
        }
    }
}