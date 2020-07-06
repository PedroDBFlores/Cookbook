package web.recipetype

import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.CreateResult
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.CreateRecipeType
import web.ResponseError

class CreateRecipeTypeHandler(private val createRecipeType: CreateRecipeType) : Handler {

    @OpenApi(
        summary = "Create recipe type",
        description = "Creates a new recipe type",
        operationId = "CreateRecipeType",
        method = HttpMethod.POST,
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(
                from = CreateRecipeTypeRepresenter::class
            )],
            required = true,
            description = "The required information to create a new recipe type"
        ),
        responses = [
            OpenApiResponse(
                status = "201",
                description = "A recipe type was created sucessfully, returning it's id",
                content = [OpenApiContent(from = CreateResult::class)]
            ),
            OpenApiResponse(
                status = "400",
                description = "When an error ocurred parsing the body",
                content = [OpenApiContent(from = ResponseError::class)]
            )
        ],
        tags = ["RecipeType"]
    )
    override fun handle(ctx: Context) {
        val recipeType = ctx.bodyValidator<CreateRecipeTypeRepresenter>()
            .get()
            .toRecipeType()
        val id = createRecipeType(recipeType)
        ctx.status(HttpStatus.CREATED_201).json(CreateResult(id))
    }

    private data class CreateRecipeTypeRepresenter(val name: String) {
        fun toRecipeType() = RecipeType(id = 0, name = name)
    }
}
