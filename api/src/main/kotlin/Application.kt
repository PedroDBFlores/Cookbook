import com.sksamuel.hoplite.ConfigLoader
import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import org.flywaydb.core.Flyway
import server.CookbookApi

/** Application entry point */
fun main() = ConfigLoader().loadConfigOrThrow<ConfigurationFile>("/application.conf")
    .run {
        migrateDB(this)
        CookbookApi(configuration = this).start()
    }

/** Executes the database migrations present on the /resources folder with Flyway
 * @param configuration The configuration file for the Cookbook API
 */
private fun migrateDB(configuration: ConfigurationFile) =
    HikariDataSource().run {
        jdbcUrl = configuration.database.jdbcUrl
        Flyway.configure()
            .dataSource(this)
            .load()
            .migrate()
    }
