package server

import com.fasterxml.jackson.databind.SerializationFeature
import config.ConfigurationFile
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import server.modules.contentNegotiationModule
import server.modules.dependencyInjectionModule
import server.modules.routingModule

/**
 * Defines the Cookbook API
 */
class CookbookApi(
    config: ConfigurationFile
) : AutoCloseable {
    private val server = embeddedServer(Netty, config.api.port) {
        contentNegotiationModule()
        dependencyInjectionModule()
        routingModule()
    }

    init {
        server.start(wait = true)
    }

    override fun close() {
        server.stop(5000,5500)
    }
}
