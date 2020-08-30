package web.recipetype

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipe.CreateRecipe
import usecases.recipetype.CreateRecipeType

class CreateRecipeTypeHandler(private val createRecipeType: CreateRecipeType) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val parameters = call.receiveOrThrow<CreateRecipeTypeRepresenter>()
            .toParameters()
        val id = createRecipeType(parameters)
        call.respond(HttpStatusCode.Created, CreateResult(id))
    }

    private data class CreateRecipeTypeRepresenter(val name: String) {
        init {
            check(name.isNotBlank()) { "Field 'name' must not be empty or blank" }
        }

        fun toParameters() = CreateRecipeType.Parameters(name = name)
    }
}
