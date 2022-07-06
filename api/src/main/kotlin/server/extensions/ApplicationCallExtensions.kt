package server.extensions

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*

/**
 * Tries to receive the requested type and returns if it's able to build it
 * @param T The type of the object to receive
 * @return The built object
 * @throws BadRequestException If the object is not able to be deserialized
 */
suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(): T =
    runCatching { receive<T>() }
        .onFailure { throw BadRequestException(it.message!!) }.getOrThrow()

/**
 * Tries to receive the requested type and returns if it's able to build it
 * @param T The type of the object to receive
 * @param validations The validations to be applied to the received body
 * @return The built object
 * @throws BadRequestException If the object is not able to be deserialized OR the validations fail
 */
suspend inline fun <reified T : Any> ApplicationCall.receiveOrThrow(validations: (T) -> Unit = {}): T =
    runCatching {
        val receivedBody = receive<T>()
        validations(receivedBody)
        receivedBody
    }.onFailure { throw BadRequestException(it.message!!) }.getOrThrow()
