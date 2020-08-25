import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import org.flywaydb.core.Flyway
import org.kodein.di.instance
import org.kodein.di.ktor.di
import server.CookbookApi

fun main() {
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")
    migrateDB(configuration)
    CookbookApi(configuration = configuration)
}

private fun migrateDB(configuration: ConfigurationFile) {
    with(configuration.database) {
        val flyway = Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .load()

        flyway.migrate()
    }
}

