import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import pt.pedro.cookbook.server.modules.authenticationModule
import pt.pedro.cookbook.server.modules.jsonModule
import pt.pedro.cookbook.server.modules.kodeinModule
import pt.pedro.cookbook.server.modules.routingModule
import pt.pedro.cookbook.server.serverModule

object IntegrationTestUtils  {
    fun getTestServer() : Application.() -> Unit {
        return  {
            (environment.config as MapApplicationConfig).apply {
                serverModule(true)
                authenticationModule(true)
                jsonModule(true)
                kodeinModule(true)
                routingModule(true)
            }
        }
    }
}