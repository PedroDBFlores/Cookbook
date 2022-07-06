package web.recipetype

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ports.KtorHandler
import usecases.recipetype.GetAllRecipeTypes

class GetAllRecipeTypesHandler(private val getAllRecipeTypes: GetAllRecipeTypes) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) = call.respond(HttpStatusCode.OK, getAllRecipeTypes())
}
