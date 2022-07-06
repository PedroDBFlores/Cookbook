package web.recipe

import errors.RecipeNotFound
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.util.*
import ports.KtorHandler
import usecases.recipe.FindRecipe

class FindRecipeHandler(private val findRecipe: FindRecipe) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) =
        try {
            val recipeId = call.parameters.getOrFail<Int>("id")
            require(recipeId > 0) { throw BadRequestException("Path param 'id' must be bigger than 0") }

            val recipe = findRecipe(FindRecipe.Parameters(recipeId))
            call.respond(HttpStatusCode.OK, recipe)
        } catch (ex: RecipeNotFound) {
            call.respond(HttpStatusCode.NotFound)
        }
}
