package server.modules

import io.ktor.application.*
import io.ktor.features.*

fun Application.defaultHeadersModule() {
    install(DefaultHeaders) {
        header("Server", "Cookbook Ktor Server")
        header("X-CreatedBy", "Mr. Flowers")
        header("Access-Control-Allow-Origin", "http://localhost:8080")
        header("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type, Accept")
    }
}