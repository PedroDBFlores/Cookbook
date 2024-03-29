package web.recipetype

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import model.CreateResult
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipetype.CreateRecipeType

class CreateRecipeTypeHandler(private val createRecipeType: CreateRecipeType) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val parameters = call.receiveOrThrow<CreateRecipeTypeRepresenter>()
            .toParameters()
        val id = createRecipeType(parameters)
        call.respond(HttpStatusCode.Created, CreateResult(id))
    }

    @Serializable
    private data class CreateRecipeTypeRepresenter(val name: String) {
        init {
            check(name.isNotBlank()) { "Field 'name' must not be empty or blank" }
        }

        fun toParameters() = CreateRecipeType.Parameters(name = name)
    }
}
