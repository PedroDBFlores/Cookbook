package web

import config.ConfigurationFile
import config.Router
import io.javalin.Javalin
import io.javalin.core.plugin.Plugin
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

/**
 * Defines the Cookbook API
 */
class CookbookApi(
    private val config: ConfigurationFile,
    private val javalinPlugins: List<Plugin>,
    private val router: Router,
    private val onStop: () -> Unit = {}
) : AutoCloseable {
    private val app: Javalin

    init {
        app = initializeApp()
        router.register(app)
    }

    private fun initializeApp(): Javalin = Javalin.create { config ->
        javalinPlugins.forEach { config.registerPlugin(it) }
    }
        .exception(BadRequestResponse::class.java) { ex, ctx ->
            handleError(ex, ctx)
        }
        .exception(Exception::class.java) { ex, ctx ->
            handleError(ex, ctx)
        }.events {
            it.serverStopping {
                onStop()
            }
        }
        .after(::enableStrictTransportSecurity)

    //region Methods
    fun start() {
        app.start(config.api.port)
    }

    override fun close() {
        app.stop()
    }

    private fun enableStrictTransportSecurity(context: Context) {
        if (context.header("X-Forwarded-Proto") == "https") {
            context.header("Strict-Transport-Security", "max-age=31536000")
        }
    }

    private fun handleError(ex: Exception, ctx: Context) {
        println("${ex.javaClass}\n${ex.message}")
        when (ex) {
            is BadRequestResponse -> ctx.status(HttpStatus.BAD_REQUEST_400).error(
                code = "BAD_REQUEST",
                message = ex.message.toString()
            )
            else -> ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500).error(
                code = "INTERNAL_SERVER_ERROR",
                message = "Unexpected error (${ex.javaClass.simpleName}): ${ex.message}"
            )
        }
    }
    //endregion Methods
}
