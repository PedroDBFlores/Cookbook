package web.recipetype

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipetype.UpdateRecipeType

class UpdateRecipeTypeHandler(private val updateRecipeType: UpdateRecipeType) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val parameters = call.receiveOrThrow<UpdateRecipeTypeRepresenter>()
            .toParameters()
        updateRecipeType(parameters)
        call.respond(HttpStatusCode.OK)
    }

    data class UpdateRecipeTypeRepresenter(val id: Int, val name: String) {
        init {
            check(id > 0) { "Field 'id' must be bigger than zero" }
            check(name.isNotBlank()) { "Field 'name' must not be empty" }
        }

        fun toParameters() = UpdateRecipeType.Parameters(id = id, name = name)
    }
}
