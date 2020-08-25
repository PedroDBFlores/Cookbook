package adapters.database

import adapters.authentication.ApplicationRoles
import adapters.database.schema.Roles
import adapters.database.schema.UserRoles
import adapters.database.schema.Users
import errors.WrongCredentials
import errors.UserNotFound
import model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.HashingService
import ports.UserRepository

class UserRepositoryImpl(
    private val database: Database,
    private val hashingService: HashingService
) : UserRepository {
    override fun find(id: Int): User? = transaction(database) {
        findQuery { this.select { Users.id eq id } }
            .mapNotNull(::mapToUser)
            .firstOrNull()
    }

    override fun find(userName: String): User? = transaction(database) {
        findQuery { this.select { Users.userName eq userName } }
            .mapNotNull(::mapToUser)
            .firstOrNull()
    }

    private fun findQuery(selectFilter: FieldSet.() -> Query) =
        (Users leftJoin UserRoles leftJoin Roles).slice(
            Users.id,
            Users.name,
            Users.userName,
            Users.passwordHash,
            Roles.code.groupConcat(";")
        )
            .selectFilter()
            .groupBy(Users.id, Users.name, Users.userName, Users.passwordHash, Roles.code)

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
                throw WrongCredentials()
            }

            hashingService.hash(newPassword)
        } ?: currentUser.passwordHash

        Users.update({ Users.id eq user.id }) { userToUpdate ->
            userToUpdate[name] = user.name
            userToUpdate[passwordHash] = passwordHashToUpdate
        }
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        UserRoles.deleteWhere { UserRoles.userId eq id }
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun mapToUser(row: ResultRow) = User(
        id = row[Users.id].value,
        name = row[Users.name],
        userName = row[Users.userName],
        passwordHash = row[Users.passwordHash],
        roles = row.getOrNull(Roles.code.groupConcat(";"))?.run {
            split(";")
        } ?: listOf()
    )
}
