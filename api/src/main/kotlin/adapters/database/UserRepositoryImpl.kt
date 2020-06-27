package adapters.database

import adapters.database.schema.Users
import errors.UserNotFound
import model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.HashingService
import ports.PasswordMismatchError
import ports.UserRepository

class UserRepositoryImpl(
    private val database: Database,
    private val hashingService: HashingService
) : UserRepository {
    override fun find(id: Int): User? = transaction(database) {
        Users.select { Users.id eq id }
            .mapNotNull { row -> mapToUser(row) }
            .firstOrNull()
    }

    override fun find(userName: String): User? = transaction(database) {
        Users.select { Users.userName eq userName }
            .mapNotNull { row -> mapToUser(row) }
            .firstOrNull()
    }

    override fun create(user: User, userPassword: String): Int = transaction(database) {
        Users.insertAndGetId { userToCreate ->
            userToCreate[name] = user.name
            userToCreate[userName] = user.userName
            userToCreate[passwordHash] = hashingService.hash(userPassword)
        }.value
    }

    override fun update(user: User, oldPassword: String?, newPassword: String?): Unit = transaction(database) {
        val currentUser = find(user.id)
        require(currentUser != null) { throw UserNotFound(user.id) }

        val passwordHashToUpdate = newPassword?.let {
            require(oldPassword != null)
            if (!hashingService.verify(oldPassword, currentUser.passwordHash)) {
                throw PasswordMismatchError()
            }

            hashingService.hash(newPassword)
        } ?: currentUser.passwordHash

        Users.update({ Users.id eq user.id }) { userToUpdate ->
            userToUpdate[name] = user.name
            userToUpdate[passwordHash] = passwordHashToUpdate
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun mapToUser(row: ResultRow) = User(
        id = row[Users.id].value,
        name = row[Users.name],
        userName = row[Users.userName],
        passwordHash = row[Users.passwordHash]
    )
}