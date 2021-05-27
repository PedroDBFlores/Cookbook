package web.recipetype

import errors.RecipeTypeNotFound
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import ports.KtorHandler
import usecases.recipetype.FindRecipeType

class FindRecipeTypeHandler(private val findRecipeType: FindRecipeType) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) =
        try {
            val recipeTypeId = call.parameters.getOrFail<Int>("id")
            require(recipeTypeId > 0) { throw BadRequestException("Path param 'id' must be bigger than 0") }

            val recipeType = findRecipeType(FindRecipeType.Parameters(recipeTypeId))
            call.respond(HttpStatusCode.OK, recipeType)
        } catch (ex: RecipeTypeNotFound) {
            call.respond(HttpStatusCode.NotFound)
        }
}

