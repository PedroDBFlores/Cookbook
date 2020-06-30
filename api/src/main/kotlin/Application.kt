import config.ConfigurationFile
import config.KoinModules.applicationModules
import config.Router
import io.javalin.core.plugin.Plugin
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import web.CookbookApi

fun main() {
    CookbookApi(
        config = CookbookApiBuilder.config,
        javalinPlugins = CookbookApiBuilder.javalinPlugins,
        router = CookbookApiBuilder.router,
        onStop = { stopKoin() }
    ).start()
}

internal object CookbookApiBuilder : KoinComponent {
    val config: ConfigurationFile by inject()
    val javalinPlugins: List<Plugin> by inject()
    val router: Router by inject()

    init {
        startKoin {
            modules(applicationModules)
        }
    }
}
