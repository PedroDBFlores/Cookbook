package web.recipe

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import model.CreateResult
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipe.CreateRecipe

class CreateRecipeHandler(private val createRecipe: CreateRecipe) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) =
        call.receiveOrThrow<CreateRecipeRepresenter>().toParameters()
            .run { createRecipe(this) }
            .run { call.respond(HttpStatusCode.Created, CreateResult(this)) }
}

@Serializable
private data class CreateRecipeRepresenter(
    val recipeTypeId: Int,
    val name: String,
    val description: String,
    val ingredients: String,
    val preparingSteps: String
) {
    init {
        check(recipeTypeId > 0) { "Field 'recipeTypeId' must be bigger than 0" }
        check(name.isNotBlank()) { "Field 'name' must not be empty or blank" }
        check(description.isNotBlank()) { "Field 'description' must not be empty or blank" }
        check(ingredients.isNotBlank()) { "Field 'ingredients' must not be empty or blank" }
        check(preparingSteps.isNotBlank()) { "Field 'preparingSteps' must not be empty or blank" }
    }

    fun toParameters() = CreateRecipe.Parameters(
        recipeTypeId = recipeTypeId,
        name = name,
        description = description,
        ingredients = ingredients,
        preparingSteps = preparingSteps
    )
}
