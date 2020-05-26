package pt.pedro.cookbook.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import pt.pedro.cookbook.common.FileUtils

object DatabaseManager {
    //region Fields
    val db by lazy {
        val conn = parseDatabaseConnection(FileUtils.readResourceAsString("database.json"))
        val dataSource = HikariDataSource()
        with(conn) {
            dataSource.driverClassName = driver
            dataSource.jdbcUrl = jdbcUrl
            dataSource.username = userName
            dataSource.password = password
        }
        Database.connect(dataSource)
    }
    //endregion Fields

    //region Methods

    suspend fun runInTransaction(block: suspend () -> Unit) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            block()
        }
    }

    suspend fun <T> returnFromTransaction(block: suspend () -> T): T {
        return newSuspendedTransaction {
            var result: T? = null
            try {
                result = block()
                commit()
            } catch (e: Exception) {
                rollback()
            }
            return@newSuspendedTransaction result!!
        }
    }

    private fun parseDatabaseConnection(json: String): DatabaseConnection {
        return ObjectMapper().readValue(json, DatabaseConnection::class.java)
    }
    //endregion Methods
}

private data class DatabaseConnection(
    @JsonProperty("driver") val driver: String,
    @JsonProperty("jdbcUrl") val jdbcUrl: String,
    @JsonProperty("username") val userName: String,
    @JsonProperty("password") val password: String
)
