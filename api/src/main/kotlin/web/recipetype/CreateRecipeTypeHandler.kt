package web.recipetype

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import model.RecipeType
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipetype.CreateRecipeType

class CreateRecipeTypeHandler(private val createRecipeType: CreateRecipeType) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipeType = call.receiveOrThrow<CreateRecipeTypeRepresenter>()
            .toRecipeType()
        val id = createRecipeType(CreateRecipeType.Parameters(recipeType))
        call.respond(HttpStatusCode.Created, CreateResult(id))
    }

    private data class CreateRecipeTypeRepresenter(val name: String) {
        fun toRecipeType() = RecipeType(name = name)
    }
}
