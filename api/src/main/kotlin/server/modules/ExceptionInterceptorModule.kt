package server.modules

import errors.OperationNotAllowed
import errors.ResourceAlreadyExists
import errors.ResourceNotFound
import errors.ValidationError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeoutException

fun Application.exceptionInterceptorModule() = intercept(ApplicationCallPipeline.ApplicationPhase.Call) {
    try {
        proceed()
    } catch (unhandledException: Throwable) {
        val httpStatusCode = when (unhandledException) {
            is BadRequestException, is ValidationError -> HttpStatusCode.BadRequest
            is OperationNotAllowed -> HttpStatusCode.Forbidden
            is NotFoundException, is ResourceNotFound -> HttpStatusCode.NotFound
            is ResourceAlreadyExists -> HttpStatusCode.Conflict
            is UnsupportedMediaTypeException -> HttpStatusCode.UnsupportedMediaType
            is TimeoutException, is TimeoutCancellationException -> HttpStatusCode.GatewayTimeout
            else -> HttpStatusCode.InternalServerError
        }
        call.respond(httpStatusCode, ResponseError(httpStatusCode.value.toString(), unhandledException.message!!))
    }
}

@Serializable
data class ResponseError(
    val code: String,
    val message: String
)
