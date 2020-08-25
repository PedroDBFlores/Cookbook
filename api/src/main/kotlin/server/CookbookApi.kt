package server

import config.ConfigurationFile
import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import model.User
import org.kodein.di.instance
import org.kodein.di.ktor.di
import ports.JWTManager
import server.modules.*

/**
 * Defines the Cookbook API
 */
class CookbookApi(
    configuration: ConfigurationFile
) : AutoCloseable {
    private val server = embeddedServer(Netty, configuration.api.port) {
        contentNegotiationModule()
        exceptionInterceptorModule()
        dependencyInjectionModule(configuration)
        val userJWTManager by di().instance<JWTManager<User>>("userJWTManager")
        val adminJWTManager by di().instance<JWTManager<User>>("adminJWTManager")
        jwtModule(userJwtManager = userJWTManager, adminJWTManager = adminJWTManager)
        routingModule()
        defaultHeadersModule()
    }

    init {
        server.start(wait = true)
    }

    override fun close() {
        server.stop(5000, 5500)
    }
}
