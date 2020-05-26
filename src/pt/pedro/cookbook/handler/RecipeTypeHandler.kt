package pt.pedro.cookbook.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import pt.pedro.cookbook.service.RecipeTypeService

internal class RecipeTypeHandler(private val recipeTypeService: RecipeTypeService) : Handler(), CrudHandler {
    override suspend fun get(call: ApplicationCall) {
        try {
            val idParam =
                call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest, "Id is required")
            val id = Integer.parseInt(idParam)
            val recipeType = recipeTypeService.get(id)
            call.respond(HttpStatusCode.OK, recipeType)
        } catch (ex: Exception) {
            handleException(call, ex)
        }
    }

    override suspend fun getAll(call: ApplicationCall) {
        TODO("Not yet implemented")
    }

    override suspend fun create(call: ApplicationCall) {
        TODO("Not yet implemented")
    }

    override suspend fun update(call: ApplicationCall) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(call: ApplicationCall) {
        TODO("Not yet implemented")
    }
}