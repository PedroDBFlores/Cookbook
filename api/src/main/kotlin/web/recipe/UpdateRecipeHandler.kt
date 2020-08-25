package web.recipe

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.Recipe
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipe.UpdateRecipe

class UpdateRecipeHandler(private val updateRecipe: UpdateRecipe) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipe = call.receiveOrThrow<UpdateRecipeRepresenter>()
            .asRecipe()
        updateRecipe(UpdateRecipe.Parameters(recipe))
        call.respond(HttpStatusCode.OK)
    }

    private data class UpdateRecipeRepresenter(
        val id: Int,
        val recipeTypeId: Int,
        val userId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String
    ) {
        private val recipe = Recipe(
            id = id,
            recipeTypeId = recipeTypeId,
            userId = userId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )

        init {
            check(id > 0) { "Field 'id' must be bigger than zero" }
        }

        fun asRecipe() = recipe
    }
}
