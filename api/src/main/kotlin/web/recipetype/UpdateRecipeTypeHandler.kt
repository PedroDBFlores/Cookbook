package web.recipetype

import com.fasterxml.jackson.annotation.JsonProperty
import errors.ValidationError
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.RecipeType
import org.eclipse.jetty.http.HttpStatus

import usecases.recipetype.UpdateRecipeType
import web.ResponseError
import web.error

internal class UpdateRecipeTypeHandler(private val updateRecipeType: UpdateRecipeType) : Handler {

    @OpenApi(
        description = "Updates an existing recipe type",
        method = HttpMethod.PUT,
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
        try {
            val recipeType = ctx.body<UpdateRecipeTypeRepresenter>().toRecipeType()
            updateRecipeType(recipeType)
            ctx.status(HttpStatus.OK_200)
        } catch (ex: BadRequestResponse) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                .error("BAD_REQUEST", "Invalid JSON format")
        } catch (e: ValidationError) {
            val field = e.javaClass.simpleName.removePrefix("Invalid")
            ctx.status(HttpStatus.BAD_REQUEST_400)
                .error("BAD_REQUEST", "Invalid $field")
        }
    }

    data class UpdateRecipeTypeRepresenter(@JsonProperty("id") val id: Int, @JsonProperty("name") val name: String) {
        init {
            require(id > 0) { throw ValidationError("id") }
            require(name.isNotEmpty()) { throw ValidationError("name") }
        }

        fun toRecipeType() = RecipeType(id = id, name = name)
    }
}