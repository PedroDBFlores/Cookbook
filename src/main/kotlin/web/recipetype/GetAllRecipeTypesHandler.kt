package web.recipetype

import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.GetAllRecipeTypes

internal class GetAllRecipeTypesHandler(private val getAllRecipeTypes: GetAllRecipeTypes) : Handler {

    @OpenApi(
        description = "Gets all the recipe types",
        method = HttpMethod.GET,
        responses = [OpenApiResponse(
            status = "200",
            content = [OpenApiContent(from = Array<RecipeType>::class)]
        )],
        tags = ["RecipeType"]
    )
    override fun handle(ctx: Context) {
        val recipeTypes = getAllRecipeTypes()
        ctx.status(HttpStatus.OK_200).json(recipeTypes)
    }
}