package web.recipetype

import com.fasterxml.jackson.annotation.JsonProperty
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.CreateRecipeType
import web.ResponseError

internal class CreateRecipeTypeHandler(private val createRecipeType: CreateRecipeType) : Handler {

    @OpenApi(
        description = "Creates a new recipe type",
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
                content = [OpenApiContent(from = Int::class)]
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
            .check({ rep -> rep.name.isNotEmpty() }, "Field 'name' cannot be empty")
            .get()
            .toRecipeType()
        val id = createRecipeType(recipeType)
        ctx.status(HttpStatus.CREATED_201).result(id.toString())
    }

    private data class CreateRecipeTypeRepresenter(@JsonProperty("name") val name: String) {
        fun toRecipeType() = RecipeType(id = 0, name = name)
    }
}

