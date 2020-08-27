package server.modules

import errors.OperationNotAllowed
import errors.UserAlreadyExists
import errors.ValidationError
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import utils.JsonHelpers.toJson
import java.sql.SQLException
import java.util.concurrent.TimeoutException

class ExceptionIntercepterModuleTest : DescribeSpec({
    fun createTestServerForCallPhase(action: () -> Nothing): Application.() -> Unit = {
        contentNegotiationModule()
        exceptionInterceptorModule()
        routing {
            get("*") {
                action()
            }
        }
    }

    describe("Exception interceptor module test") {
        arrayOf(
            row(
                { throw BadRequestException("BAD_REQUEST") },
                HttpStatusCode.BadRequest,
                ResponseError(HttpStatusCode.BadRequest.value.toString(), "BAD_REQUEST"),
                "a BadRequestException (Ktor) occurs"
            ),
            row(
                { throw OperationNotAllowed("You are not allowed") },
                HttpStatusCode.Forbidden,
                ResponseError(HttpStatusCode.Forbidden.value.toString(), "You are not allowed"),
                "a OperationNotAllowed occurs"
            ),
            row(
                { throw NotFoundException() },
                HttpStatusCode.NotFound,
                ResponseError(HttpStatusCode.NotFound.value.toString(), "Resource not found"),
                "a NotFoundException (Ktor) occurs"
            ),
            row(
                { throw UnsupportedMediaTypeException(ContentType.Any) },
                HttpStatusCode.UnsupportedMediaType,
                ResponseError(
                    HttpStatusCode.UnsupportedMediaType.value.toString(),
                    "Content type */* is not supported"
                ),
                "a UnsupportedMediaTypeException (Ktor) occurs"
            ),
            row(
                { throw TimeoutException("Timeout") },
                HttpStatusCode.GatewayTimeout,
                ResponseError(HttpStatusCode.GatewayTimeout.value.toString(), "Timeout"),
                "a TimeoutException (Ktor) occurs"
            ),
            row(
                { throw ValidationError("id") },
                HttpStatusCode.BadRequest,
                ResponseError(HttpStatusCode.BadRequest.value.toString(), "Field 'id' is invalid"),
                "a ValidationError occurs"
            ),
            row(
                { throw UserAlreadyExists("username") },
                HttpStatusCode.Conflict,
                ResponseError(HttpStatusCode.Conflict.value.toString(), "An user with the username 'username' already exists"),
                "a ResourceAlredyExists error occurs"
            ),
            row(
                { throw SQLException("Denied") },
                HttpStatusCode.InternalServerError,
                ResponseError(HttpStatusCode.InternalServerError.value.toString(), "Denied"),
                "any other type of error occurs"
            ),
        ).forEach { (action, expectedStatusCode, expectedResponseError, description) ->
            it("returns ${expectedStatusCode.value} when $description during the Call phase") {
                withTestApplication(moduleFunction = createTestServerForCallPhase(action)) {
                    with(handleRequest(HttpMethod.Get, "/will-throw-error")) {
                        with(response) {
                            status().shouldBe(expectedStatusCode)
                            content.shouldMatchJson(expectedResponseError.toJson())
                        }
                    }
                }
            }
        }
    }
})