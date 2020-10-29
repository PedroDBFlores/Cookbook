package adapters.database

import adapters.database.schema.UserRoles
import model.UserRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ports.UserRolesRepository

class UserRolesRepositoryImpl(private val database: Database) : UserRolesRepository {
    override fun getRolesForUser(userId: Int): List<UserRole> = transaction(database) {
        UserRoles.select { UserRoles.userId eq userId }
            .mapNotNull(::mapToUserRole)
    }

    override fun addRoleToUser(userId: Int, roleId: Int): Unit = transaction(database) {
        UserRoles.insert { userRoleToCreate ->
            userRoleToCreate[UserRoles.userId] = userId
            userRoleToCreate[UserRoles.roleId] = roleId
        }
    }

    override fun deleteRoleFromUser(userId: Int, roleId: Int): Boolean = transaction(database) {
        UserRoles.deleteWhere {
            (UserRoles.userId eq userId) and (UserRoles.roleId eq roleId)
        } > 0
    }

    private fun mapToUserRole(row: ResultRow): UserRole = UserRole(
        userId = row[UserRoles.userId].value,
        roleId = row[UserRoles.roleId].value
    )
}
