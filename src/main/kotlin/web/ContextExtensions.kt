package web

import com.fasterxml.jackson.annotation.JsonProperty
import io.javalin.http.Context

fun Context.error(code: String, message: String) {
    json(ResponseError(code, message))
}

data class ResponseError(
    @JsonProperty("code") val code: String,
    @JsonProperty("message") val message: String
)



