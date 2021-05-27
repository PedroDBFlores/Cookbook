package web.recipetype

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import usecases.recipetype.GetAllRecipeTypes

class GetAllRecipeTypesHandler(private val getAllRecipeTypes: GetAllRecipeTypes) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) = call.respond(HttpStatusCode.OK, getAllRecipeTypes())
}
