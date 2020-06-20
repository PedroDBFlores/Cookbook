package web.recipe

import com.fasterxml.jackson.annotation.JsonProperty
import errors.ValidationError
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.Recipe
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.CreateRecipe
import web.ResponseError

internal class CreateRecipeHandler(private val createRecipe: CreateRecipe) : Handler {

    @OpenApi(
        description = "Creates a new recipe",
        method = HttpMethod.POST,
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(
                from = CreateRecipeRepresenter::class
            )],
            required = true,
            description = "The required information to create a new recipe"
        ),
        responses = [
            OpenApiResponse(
                status = "201",
                description = "A recipe was created sucessfully, returning it's id",
                content = [OpenApiContent(from = Int::class)]
            ),
            OpenApiResponse(
                status = "400",
                description = "When an error ocurred parsing the body",
                content = [OpenApiContent(from = ResponseError::class)]
            )
        ],
        tags = ["Recipe"]
    )
    override fun handle(ctx: Context) {
        val recipe = ctx.body<CreateRecipeRepresenter>().toRecipe()
        val id = createRecipe(recipe)
        ctx.status(HttpStatus.CREATED_201).result(id.toString())
    }

    private data class CreateRecipeRepresenter(
        @JsonProperty("recipeTypeId") val recipeTypeId: Int,
        @JsonProperty("name") val name: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("ingredients") val ingredients: String,
        @JsonProperty("preparingSteps") val preparingSteps: String
    ) {
        init {
            require(recipeTypeId > 0) { throw ValidationError("recipeTypeId") }
            require(name.isNotEmpty()) { throw ValidationError("name") }
            require(description.isNotEmpty()) { throw ValidationError("description") }
            require(ingredients.isNotEmpty()) { throw ValidationError("ingredients") }
            require(preparingSteps.isNotEmpty()) { throw ValidationError("preparingSteps") }
        }

        fun toRecipe() = Recipe(
            id = 0,
            recipeTypeId = recipeTypeId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}