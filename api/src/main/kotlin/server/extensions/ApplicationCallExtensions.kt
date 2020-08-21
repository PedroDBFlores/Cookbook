package server.extensions

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*

/**
 * Tries to receive the requested type and returns if it's able to build it
 * @param T The type of the object to receive
 * @return The built object
 */
suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(): T =
    runCatching { receive<T>() }
        .onFailure { throw BadRequestException(it.message!!) }.getOrThrow()

/**
 * Tries to receive the requested type and returns if it's able to build it
 * @param T The type of the object to receive
 * @param validations The validations to be applied to the received body
 * @return The built object
 */
suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(validations: (T) -> Unit = {}): T =
    runCatching {
        val receivedBody = receive<T>()
        validations(receivedBody)
        receivedBody
    }.onFailure { throw BadRequestException(it.message!!) }.getOrThrow()


/**
 * Calls the [Application].[respond] method with an HTTP status code, code and message, and returns a structured JSON error
 * inside the response
 * @param statusCode The [HttpStatusCode] to use
 * @param code The code to add to the JSON
 * @param message The message to add to the JSON
 */
suspend fun ApplicationCall.error(statusCode: HttpStatusCode, code: String, message: String) {
    respond(statusCode, ResponseError(code, message))
}

data class ResponseError(
    val code: String,
    val message: String
)