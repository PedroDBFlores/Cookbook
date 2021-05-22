import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import io.ktor.util.*
import org.flywaydb.core.Flyway
import server.CookbookApi

/** Application entry point */
@KtorExperimentalAPI
fun main() = ConfigLoader().loadConfigOrThrow<ConfigurationFile>("/application.conf")
    .let { conf ->
        migrateDB(conf)
        CookbookApi(configuration = conf).start()
    }

/** Executes the database migrations present on the /resources folder with Flyway
 * @param configuration The configuration file for the Cookbook API
 */
private fun migrateDB(configuration: ConfigurationFile) =
    HikariDataSource().let {
        it.jdbcUrl = configuration.database.jdbcUrl
        Flyway.configure()
            .dataSource(it)
            .load()
            .migrate()
    }
