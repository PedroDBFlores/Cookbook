package web

import io.javalin.core.security.Role
import io.javalin.http.Context

//region Authorization
enum class CookbookRoles : Role {
    ANYONE,
    USER,
    ADMIN
}

//endregion Authorization

//region Errors
fun Context.error(code: String, message: String) {
    json(ResponseError(code, message))
}

data class ResponseError(
    val code: String,
    val message: String
)
//endregion
