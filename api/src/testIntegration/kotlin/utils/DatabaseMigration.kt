package utils

import config.ConfigurationFile
import org.flywaydb.core.Flyway

object DatabaseMigration {
    fun migrateDB(configuration: ConfigurationFile) {
        with(configuration.database) {
            val flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .load()

            flyway.migrate()
        }
    }
}
