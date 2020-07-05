package web.recipe

import errors.RecipeNotFound
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.Recipe
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.FindRecipe

class FindRecipeHandler(private val findRecipe: FindRecipe) : Handler {

    @OpenApi(
        summary = "Find recipe",
        description = "Find a recipe by id",
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
            val recipeId = ctx.pathParam("id", Int::class.java)
                .check({ id -> id > 0 }, "Path param 'id' must be bigger than 0")
                .get()
            val recipe = findRecipe(FindRecipe.Parameters(recipeId))
            ctx.status(HttpStatus.OK_200).json(recipe)
        } catch (ex: RecipeNotFound) {
            ctx.status(HttpStatus.NOT_FOUND_404)
        }
    }
}
