package web.recipe

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.Recipe
import ports.KtorHandler
import server.extensions.validateReceivedBody
import usecases.recipe.CreateRecipe

class CreateRecipeHandler(private val createRecipe: CreateRecipe) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipe = call.validateReceivedBody<CreateRecipeRepresenter> { rep ->
            check(rep.recipeTypeId > 0) { "Field 'recipeTypeId' must be bigger than zero" }
            check(rep.userId > 0) { "Field 'userId' must be bigger than zero" }
            check(rep.name.isNotEmpty()) { "Field 'name' cannot be empty" }
            check(rep.description.isNotEmpty()) { "Field 'description' cannot be empty" }
            check(rep.ingredients.isNotEmpty()) { "Field 'ingredients' cannot be empty" }
            check(rep.preparingSteps.isNotEmpty()) { "Field 'preparingSteps' cannot be empty" }
        }.toRecipe()

        val id = createRecipe(recipe)
        call.respond(HttpStatusCode.Created, mapOf("id" to id))
    }

    private data class CreateRecipeRepresenter(
        val recipeTypeId: Int,
        val userId: Int,
        val name: String,
        val description: String,
        val ingredients: String,
        val preparingSteps: String
    ) {
        fun toRecipe() = Recipe(
            recipeTypeId = recipeTypeId,
            userId = userId,
            name = name,
            description = description,
            ingredients = ingredients,
            preparingSteps = preparingSteps
        )
    }
}
