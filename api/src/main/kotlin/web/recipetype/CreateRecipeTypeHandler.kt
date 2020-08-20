package web.recipetype

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import model.RecipeType
import ports.KtorHandler
import server.extensions.validateReceivedBody
import usecases.recipetype.CreateRecipeType

class CreateRecipeTypeHandler(
    private val createRecipeType: CreateRecipeType
) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipeType = call.validateReceivedBody<CreateRecipeTypeRepresenter> {
            check(it.name.isNotEmpty()) { "Field 'name' cannot be empty" }
        }.toRecipeType()
        val id = createRecipeType(recipeType)
        call.respond(HttpStatusCode.Created, CreateResult(id))
    }

    private data class CreateRecipeTypeRepresenter(val name: String) {
        fun toRecipeType() = RecipeType(id = 0, name = name)
    }
}
