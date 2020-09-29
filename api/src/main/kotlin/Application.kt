import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.EnvironmentVariablesPropertySource
import com.typesafe.config.ConfigFactory
import config.ConfigurationFile
import io.ktor.config.*
import io.ktor.util.*
import org.flywaydb.core.Flyway
import server.CookbookApi

@KtorExperimentalAPI
fun main() {
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/application.conf")
    migrateDB(configuration)
    CookbookApi(configuration = configuration).start()
}

private fun migrateDB(configuration: ConfigurationFile) {
    with(configuration.database) {
        val flyway = Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .load()

        flyway.migrate()
    }
}
