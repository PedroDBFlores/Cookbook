package server

import config.ConfigurationFile
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import server.modules.contentNegotiationModule
import server.modules.dependencyInjectionModule
import server.modules.routingModule

/**
 * Defines the Cookbook API
 */
class CookbookApi(
    configuration: ConfigurationFile
) : AutoCloseable {
    private val server = embeddedServer(Netty, configuration.api.port) {
        contentNegotiationModule()
        dependencyInjectionModule(configuration)
        routingModule()
    }

    init {
        server.start(wait = true)
    }

    override fun close() {
        server.stop(5000,5500)
    }
}
