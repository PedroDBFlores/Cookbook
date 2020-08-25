package web.recipe

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import model.Recipe
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipe.CreateRecipe

class CreateRecipeHandler(private val createRecipe: CreateRecipe) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipe = call.receiveOrThrow<CreateRecipeRepresenter>()
            .asRecipe()
        val id = createRecipe(CreateRecipe.Parameters(recipe))
        call.respond(HttpStatusCode.Created, CreateResult(id))
    }

    private data class CreateRecipeRepresenter(
        val recipeTypeId: Int,
        val userId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String
    ) {
        private val recipe = Recipe(
            recipeTypeId = recipeTypeId,
            userId = userId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )

        fun asRecipe() = recipe
    }
}
