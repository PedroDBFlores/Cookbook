package web.recipe

import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.*
import model.Recipe
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.CreateRecipe
import web.ResponseError

class CreateRecipeHandler(private val createRecipe: CreateRecipe) : Handler {

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
        val recipe = ctx.bodyValidator<CreateRecipeRepresenter>()
            .check({ rep -> rep.recipeTypeId > 0 }, "Field 'recipeTypeId' must be bigger than zero")
            .check({ rep -> rep.name.isNotEmpty() }, "Field 'name' cannot be empty")
            .check({ rep -> rep.description.isNotEmpty() }, "Field 'description' cannot be empty")
            .check({ rep -> rep.ingredients.isNotEmpty() }, "Field 'ingredients' cannot be empty")
            .check({ rep -> rep.preparingSteps.isNotEmpty() }, "Field 'preparingSteps' cannot be empty")
            .get()
            .toRecipe()
        val id = createRecipe(recipe)
        ctx.status(HttpStatus.CREATED_201).json(mapOf("id" to id))
    }

    private data class CreateRecipeRepresenter(
        val recipeTypeId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String
    ) {
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
