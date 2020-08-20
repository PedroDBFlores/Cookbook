package server.modules

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*

fun Application.contentNegotiationModule() {
    install(ContentNegotiation){
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
