package server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

//region Authorization
//enum class CookbookRoles : Role {
//    ANYONE,
//    USER,
//    ADMIN
//}

//endregion Authorization

//region Errors
suspend fun ApplicationCall.error(statusCode: HttpStatusCode, code: String, message: String) {
    respond(statusCode, ResponseError(code, message))
}

data class ResponseError(
    val code: String,
    val message: String
)
//endregion
