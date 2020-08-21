package server.modules

import io.ktor.application.*
import io.ktor.features.*

fun Application.defaultHeadersModule() {
    install(DefaultHeaders) {
        header("Server", "Cookbook Ktor Server")
        header("X-CreatedBy", "Mr. Flowers")
    }
}