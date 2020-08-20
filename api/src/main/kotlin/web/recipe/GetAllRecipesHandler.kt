package web.recipe

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import usecases.recipe.GetAllRecipes

class GetAllRecipesHandler(private val getAllRecipes: GetAllRecipes) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipes = getAllRecipes()
        call.respond(HttpStatusCode.OK, recipes)
    }
}
