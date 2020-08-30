package web.recipe

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipe.UpdateRecipe

class UpdateRecipeHandler(private val updateRecipe: UpdateRecipe) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val parameters = call.receiveOrThrow<UpdateRecipeRepresenter>()
            .toParameters()
        updateRecipe(parameters)
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
            check(id > 0) { "Field 'id' must be bigger than 0" }
            check(recipeTypeId > 0) { "Field 'recipeTypeId' must be bigger than 0" }
            check(userId > 0) { "Field 'userId' must be bigger than 0" }
            check(name.isNotBlank()) { "Field 'name' must not be empty or blank" }
            check(description.isNotBlank()) { "Field 'description' must not be empty or blank" }
            check(ingredients.isNotBlank()) { "Field 'ingredients' must not be empty or blank" }
            check(preparingSteps.isNotBlank()) { "Field 'preparingSteps' must not be empty or blank" }
        }

        fun toParameters() = UpdateRecipe.Parameters(
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
