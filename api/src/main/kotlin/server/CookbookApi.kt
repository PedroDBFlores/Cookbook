package server

import config.ConfigurationFile
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import server.modules.*

/** Defines the Cookbook API server
 * @param configuration The configuration file for the Cookbook API.
 */
class CookbookApi(private val configuration: ConfigurationFile) : AutoCloseable {
    private val server = embeddedServer(Netty, configuration.api.port) {
        contentNegotiationModule()
        exceptionInterceptorModule()
        routingModule()
        defaultHeadersModule()
    }

    /** Starts the server at the provided port. Only waits if the application isn't on testing mode. */
    fun start() {
        println("Cookbook API started at port ${configuration.api.port}")
        server.start(wait = !configuration.api.testing)
    }

    /** Shutdowns the server. Gives a grace period of 5 seconds (0 on testing). */
    override fun close() {
        val graceStopPeriod = if (configuration.api.testing) 0 else 5000L
        server.stop(graceStopPeriod, graceStopPeriod)
        println("Cookbook API stopped")
    }
}
