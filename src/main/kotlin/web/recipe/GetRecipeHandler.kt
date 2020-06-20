package web.recipe

import errors.RecipeNotFound
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.Recipe
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.GetRecipe

internal class GetRecipeHandler(private val getRecipe: GetRecipe) : Handler {

    @OpenApi(
        description = "Gets a recipe by id",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam(name = "id", description = "Recipe id")],
        responses = [OpenApiResponse(
            status = "200",
            content = [OpenApiContent(from = Recipe::class)]
        ), OpenApiResponse(
            status = "404"
        )],
        tags = ["Recipe"]
    )
    override fun handle(ctx: Context) {
        try {
            val recipeId = ctx.pathParam("id", Int::class.java).get()
            val recipe = getRecipe(GetRecipe.Parameters(recipeId))
            ctx.status(HttpStatus.OK_200).json(recipe)
        }catch (ex:RecipeNotFound){
            ctx.status(HttpStatus.NOT_FOUND_404)
        }
    }
}