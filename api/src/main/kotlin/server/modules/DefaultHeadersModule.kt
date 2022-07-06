package server.modules

import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.defaultHeadersModule() = install(DefaultHeaders) {
    header("Server", "Cookbook Server")
    header("X-CreatedBy", "Mr. Flowers")
}
