package pt.pedro.cookbook.server.modules

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson

@Suppress("unused") // Referenced in application.conf
fun Application.jsonModule(testing: Boolean = false){
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)

            registerModule(JodaModule())
        }
    }
}