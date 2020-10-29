import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.ktor.util.*
import org.flywaydb.core.Flyway
import server.CookbookApi

/** Application entry point */
@KtorExperimentalAPI
fun main() {
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/application.conf")
    migrateDB(configuration)
    CookbookApi(configuration = configuration).start()
}

/** Executes the database migrations present on the /resources folder with Flyway
 * @param configuration The configuration file for the Cookbook API
 */
private fun migrateDB(configuration: ConfigurationFile) {
    with(configuration.database) {
        val flyway = Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .load()

        flyway.migrate()
    }
}
