package pt.pedro.cookbook.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import pt.pedro.cookbook.exception.service.EntityNotFoundException

/**
 * Base class for an endpoint handler
 */
internal abstract class Handler {

    /**
     * Handles the errors from the service layer or the handler
     * @param
     * @param ex The thrown exception
     */
    suspend fun handleException(call: ApplicationCall, ex: Exception) {
        when (ex) {
            is EntityNotFoundException ->
                call.respond(HttpStatusCode.NotFound, ex.message!!)
            else -> call.respond(HttpStatusCode.InternalServerError, ex.message!!)
        }
    }
}