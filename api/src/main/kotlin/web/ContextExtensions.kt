package web

import com.fasterxml.jackson.annotation.JsonProperty
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
    @JsonProperty("code") val code: String,
    @JsonProperty("message") val message: String
)
//endregion



