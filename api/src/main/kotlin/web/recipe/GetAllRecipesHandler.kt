package web.recipe

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ports.KtorHandler
import usecases.recipe.GetAllRecipes

class GetAllRecipesHandler(private val getAllRecipes: GetAllRecipes) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) = call.respond(HttpStatusCode.OK, getAllRecipes())
}
