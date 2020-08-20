package server.extensions

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*

suspend inline fun <reified T : Any> ApplicationCall.validateReceivedBody(validations: (T) -> Unit = {}): T {
    lateinit var receivedBody: T
    runCatching {
        receivedBody = receive()
        validations(receivedBody)
    }.onFailure { throw BadRequestException(it.message!!) }

    return receivedBody
}