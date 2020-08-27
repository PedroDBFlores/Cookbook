package server.modules

import errors.OperationNotAllowed
import errors.ResourceAlreadyExists
import errors.ValidationError
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.coroutines.TimeoutCancellationException
import java.util.concurrent.TimeoutException

fun Application.exceptionInterceptorModule() {
    intercept(ApplicationCallPipeline.ApplicationPhase.Call) {
        try {
            proceed()
        } catch (unhandledException: Throwable) {
            val httpStatusCode = when (unhandledException) {
                is BadRequestException, is ValidationError -> HttpStatusCode.BadRequest
                is OperationNotAllowed -> HttpStatusCode.Forbidden
                is NotFoundException -> HttpStatusCode.NotFound
                is ResourceAlreadyExists -> HttpStatusCode.Conflict
                is UnsupportedMediaTypeException -> HttpStatusCode.UnsupportedMediaType
                is TimeoutException, is TimeoutCancellationException -> HttpStatusCode.GatewayTimeout
                else -> HttpStatusCode.InternalServerError
            }
            call.respond(httpStatusCode, ResponseError(httpStatusCode.value.toString(), unhandledException.message!!))
        }
    }
}

data class ResponseError(
    val code: String,
    val message: String
)