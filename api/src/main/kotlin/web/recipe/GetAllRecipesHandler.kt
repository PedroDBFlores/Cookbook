package web.recipe

import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.HttpMethod
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import model.Recipe
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.GetAllRecipes

class GetAllRecipesHandler(private val getAllRecipes: GetAllRecipes) : Handler {

    @OpenApi(
        description = "Gets all the recipes",
        method = HttpMethod.GET,
        responses = [OpenApiResponse(
            status = "200",
            content = [OpenApiContent(from = Array<Recipe>::class)]
        )],
        tags = ["Recipe"]
    )
    override fun handle(ctx: Context) {
        val recipes = getAllRecipes()
        ctx.status(HttpStatus.OK_200).json(recipes)
    }
}
