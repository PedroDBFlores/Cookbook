package pt.pedro.cookbook.handler

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import pt.pedro.cookbook.dto.RecipeType
import pt.pedro.cookbook.service.RecipeTypeService

internal class RecipeTypeHandler(private val service: RecipeTypeService) : Handler(), CrudHandler {
    override suspend fun get(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]!!.toIntOrNull() ?: return call.respond(
                HttpStatusCode.BadRequest,
                "The id provided is invalid"
            )
            val recipeType = service.get(id)
            call.respond(HttpStatusCode.OK, recipeType)
        } catch (ex: Exception) {
            handleException(call, ex)
        }
    }

    override suspend fun getAll(call: ApplicationCall) {
        try {
            call.respond(HttpStatusCode.OK, service.getAll())
        } catch (ex: Exception) {
            handleException(call, ex)
        }
    }

    override suspend fun create(call: ApplicationCall) {
        try {
            val recipeType = call.receive(RecipeType::class)
            if (recipeType.name.isEmpty()) {
                return call.respond(HttpStatusCode.BadRequest, "Name cannot be empty")
            }

            call.respond(HttpStatusCode.Created, service.create(recipeType))
        } catch (ex: Exception) {
            if (ex is MissingKotlinParameterException || ex is MismatchedInputException) {
                return call.respond(HttpStatusCode.BadRequest, "The provided JSON hasn't got the needed structure")
            }
            handleException(call, ex)
        }
    }

    override suspend fun update(call: ApplicationCall) {
        try {
            val recipeType = call.receive(RecipeType::class)
            if (recipeType.id <= 0) return call.respond(HttpStatusCode.BadRequest, "Id cannot be 0")
            if (recipeType.name.isEmpty()) return call.respond(HttpStatusCode.BadRequest, "Name cannot be empty")


            call.respond(HttpStatusCode.OK, service.update(recipeType))
        } catch (ex: Exception) {
            if (ex is MissingKotlinParameterException || ex is MismatchedInputException) {
                return call.respond(HttpStatusCode.BadRequest, "The provided JSON hasn't got the needed structure")
            }
            handleException(call, ex)
        }
    }

    override suspend fun delete(call: ApplicationCall) {
        try {
            val id = call.parameters["id"]!!.toIntOrNull() ?: return call.respond(
                HttpStatusCode.BadRequest,
                "The id provided is invalid"
            )
            val deleted = service.delete(id)
            call.respond(HttpStatusCode.OK, deleted)
        } catch (ex: Exception) {
            handleException(call, ex)
        }
    }
}