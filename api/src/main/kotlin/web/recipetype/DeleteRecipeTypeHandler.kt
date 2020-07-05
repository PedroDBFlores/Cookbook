package web.recipetype

import errors.RecipeTypeNotFound
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.HttpMethod
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiParam
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.DeleteRecipeType

class DeleteRecipeTypeHandler(private val deleteRecipeType: DeleteRecipeType) : Handler {

    @OpenApi(
        summary = "Delete recipe type",
        description = "Deletes a recipe type by id",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam(name = "id", description = "Recipe type id")],
        responses = [OpenApiResponse(
            status = "204"
        ), OpenApiResponse(
            status = "404",
            description = "When the recipe type to delete wasn't found"
        )],
        tags = ["RecipeType"]
    )
    override fun handle(ctx: Context) {
        try {
            val recipeTypeId = ctx.pathParam("id", Int::class.java)
                .check({ id -> id > 0 }, "Path param 'id' must be bigger than 0")
                .get()
            deleteRecipeType(DeleteRecipeType.Parameters(recipeTypeId))
            ctx.status(HttpStatus.NO_CONTENT_204)
        } catch (ex: RecipeTypeNotFound) {
            ctx.status(HttpStatus.NOT_FOUND_404)
        }
    }
}
