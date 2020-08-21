package server.extensions

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

suspend inline fun <reified T : Any> ApplicationCall.validateReceivedBody(validations: (T) -> Unit = {}): T =
    runCatching {
        val receivedBody = receive<T>()
        validations(receivedBody)
        receivedBody
    }.onFailure { throw BadRequestException(it.message!!) }.getOrThrow()


suspend fun ApplicationCall.error(statusCode: HttpStatusCode, code: String, message: String) {
    respond(statusCode, ResponseError(code, message))
}

data class ResponseError(
    val code: String,
    val message: String
)