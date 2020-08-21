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
            .toRecipe()
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
        init {
            check(id > 0) { "Field 'id' must be bigger than zero" }
            check(recipeTypeId > 0) { "Field 'recipeTypeId' must be bigger than zero" }
            check(userId > 0) { "Field 'userId' must be bigger than zero" }
            check(name.isNotEmpty()) { "Field 'name' must not be empty" }
            check(description.isNotEmpty()) { "Field 'description' must not be empty" }
            check(ingredients.isNotEmpty()) { "Field 'ingredients' must not be empty" }
            check(preparingSteps.isNotEmpty()) { "Field 'preparingSteps' must not be empty" }
        }

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
