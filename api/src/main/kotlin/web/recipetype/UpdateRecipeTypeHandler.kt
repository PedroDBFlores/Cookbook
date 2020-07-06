package web.recipetype

import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.UpdateRecipeType
import web.ResponseError

class UpdateRecipeTypeHandler(private val updateRecipeType: UpdateRecipeType) : Handler {

    @OpenApi(
        description = "Updates an existing recipe type",
        method = HttpMethod.PUT,
        operationId = "UpdateRecipeType",
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(
                from = UpdateRecipeTypeRepresenter::class
            )],
            required = true,
            description = "The required information to update a recipe type"
        ),
        responses = [
            OpenApiResponse(
                status = "200"
            ),
            OpenApiResponse(
                status = "400",
                description = "When an error occurred parsing the body",
                content = [OpenApiContent(from = ResponseError::class)]
            )
        ],
        tags = ["RecipeType"]
    )
    override fun handle(ctx: Context) {
        val recipeType = ctx.bodyValidator<UpdateRecipeTypeRepresenter>()
            .check({ rep -> rep.id > 0 }, "Field 'id' must be bigger than 0")
            .get()
            .toRecipeType()
        updateRecipeType(recipeType)
        ctx.status(HttpStatus.OK_200)
    }

    data class UpdateRecipeTypeRepresenter(val id: Int, val name: String) {
        fun toRecipeType() = RecipeType(id = id, name = name)
    }
}
