package utils

import com.zaxxer.hikari.HikariDataSource
import config.ConfigurationFile
import org.flywaydb.core.Flyway

object DatabaseMigration {
    fun migrateDB(configuration: ConfigurationFile) {
        val dataSource = HikariDataSource()
        with(configuration.database) {
            dataSource.jdbcUrl = jdbcUrl
            val flyway = Flyway.configure()
                .dataSource(dataSource)
                .load()

            flyway.migrate()
        }
    }
}
