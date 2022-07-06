package web.recipetype

import errors.RecipeTypeNotFound
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.util.*
import ports.KtorHandler
import usecases.recipetype.DeleteRecipeType

class DeleteRecipeTypeHandler(private val deleteRecipeType: DeleteRecipeType) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) =
        try {
            val recipeTypeId = call.parameters.getOrFail<Int>("id")
            require(recipeTypeId > 0) { throw BadRequestException("Path param 'id' must be bigger than 0") }

            deleteRecipeType(DeleteRecipeType.Parameters(recipeTypeId))
            call.respond(HttpStatusCode.NoContent)
        } catch (ex: RecipeTypeNotFound) {
            call.respond(HttpStatusCode.NotFound)
        }
}
