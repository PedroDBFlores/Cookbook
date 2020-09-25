package adapters.database

import adapters.database.schema.UserEntity
import adapters.database.schema.Users
import errors.UserNotFound
import errors.WrongCredentials
import model.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import ports.HashingService
import ports.UserRepository

class UserRepositoryImpl(
    private val database: Database,
    private val hashingService: HashingService
) : UserRepository {
    override fun find(id: Int): User? = transaction(database) {
        UserEntity.findById(id)?.run(::mapToUser)
    }

    override fun find(userName: String): User? = transaction(database) {
        UserEntity.find { (Users.userName eq userName) }
            .map(::mapToUser)
            .firstOrNull()
    }

    override fun create(user: User, userPassword: String): Int = transaction(database) {
        UserEntity.new {
            name = user.name
            userName= user.userName
            passwordHash = hashingService.hash(userPassword)
        }.id.value
    }

    override fun update(user: User, oldPassword: String?, newPassword: String?): Unit = transaction(database) {
        val currentUser = UserEntity.findById(user.id)
        require(currentUser != null) { throw UserNotFound(user.id) }

        val passwordHashToUpdate = newPassword?.run {
            require(oldPassword != null)
            if (!hashingService.verify(oldPassword, currentUser.passwordHash)) {
                throw WrongCredentials()
            }

            hashingService.hash(newPassword)
        } ?: currentUser.passwordHash

        currentUser.name = user.name
        currentUser.passwordHash = passwordHashToUpdate
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        UserEntity.findById(id)?.run {
            delete()
            true
        } ?: false
    }

    private fun mapToUser(entity: UserEntity) = User(
        id = entity.id.value,
        name = entity.name,
        userName = entity.userName,
        passwordHash = entity.passwordHash,
        roles = entity.roles.map { it.code }
    )
}
