package server.extensions

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*

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
