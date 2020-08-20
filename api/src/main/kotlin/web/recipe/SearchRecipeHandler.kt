package web.recipe

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import model.parameters.SearchRecipeParameters
import ports.KtorHandler
import usecases.recipe.SearchRecipe

class SearchRecipeHandler(private val searchRecipe: SearchRecipe) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val parameters = call.receive<SearchRecipeParameters>()
        val results = searchRecipe(parameters)
        call.respond(HttpStatusCode.OK,results)
    }
}
