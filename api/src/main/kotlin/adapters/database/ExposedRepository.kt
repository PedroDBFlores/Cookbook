package adapters.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

typealias Action<T> = () -> T

abstract class ExposedRepository(val database: Database) {
    fun <T> runWrappedInTransaction(action: Action<T>): T = transaction(database) { action() }
}
