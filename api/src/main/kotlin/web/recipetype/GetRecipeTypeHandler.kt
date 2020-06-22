package web.recipetype

import errors.RecipeTypeNotFound
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.GetRecipeType

internal class GetRecipeTypeHandler(private val getRecipeType: GetRecipeType) : Handler {

    @OpenApi(
        summary = "Get recipe type",
        description = "Gets a recipe type by id",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam(name = "id", description = "Recipe type id")],
        responses = [OpenApiResponse(
            status = "200",
            content = [OpenApiContent(from = RecipeType::class)]
        ), OpenApiResponse(
            status = "404"
        )],
        tags = ["RecipeType"]
    )
    override fun handle(ctx: Context) {
        try {
            val recipeTypeId = ctx.pathParam("id", Int::class.java)
                .check({ id -> id > 0 }, "Path param 'id' must be bigger than 0")
                .get()
            val recipeType = getRecipeType(GetRecipeType.Parameters(recipeTypeId))
            ctx.status(HttpStatus.OK_200).json(recipeType)
        } catch (notFoundEx: RecipeTypeNotFound) {
            ctx.status(HttpStatus.NOT_FOUND_404)
        }
    }
}