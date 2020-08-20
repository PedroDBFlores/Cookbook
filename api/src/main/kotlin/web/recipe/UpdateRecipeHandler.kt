package web.recipe

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.Recipe
import ports.KtorHandler
import server.extensions.validateReceivedBody
import usecases.recipe.UpdateRecipe

class UpdateRecipeHandler(private val updateRecipe: UpdateRecipe) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipe = call.validateReceivedBody<UpdateRecipeRepresenter> {
            check(it.id > 0) { "Field 'id' must be bigger than zero" }
            check(it.recipeTypeId > 0) { "Field 'recipeTypeId' must be bigger than zero" }
        }.toRecipe()
        updateRecipe(recipe)
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
        fun toRecipe() = Recipe(
            id = id,
            recipeTypeId = recipeTypeId,
            userId = userId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}
