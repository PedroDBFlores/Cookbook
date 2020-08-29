import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import org.flywaydb.core.Flyway
import server.CookbookApi

fun main() {
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")
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

