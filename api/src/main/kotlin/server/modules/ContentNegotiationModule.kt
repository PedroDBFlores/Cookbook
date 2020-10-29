package server.modules

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*

fun Application.contentNegotiationModule() {
    install(ContentNegotiation) {
        jackson {
            KotlinModule()
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
