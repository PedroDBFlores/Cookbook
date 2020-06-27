package ports

import model.UserRole

interface UserRolesRepository {
    fun getRolesForUser(userId: Int): List<UserRole>
    fun addRoleToUser(userId: Int, roleId: Int)
    fun deleteRoleFromUser(userId: Int, roleId: Int): Boolean
}