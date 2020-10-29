package usecases.userroles

import errors.UserRoleNotFound
import ports.UserRolesRepository

class DeleteRoleFromUser(private val userRolesRepository: UserRolesRepository) {
    operator fun invoke(parameters: Parameters) {
        val (userId, roleId) = parameters

        val deleted = userRolesRepository.deleteRoleFromUser(userId, roleId)
        if (!deleted) throw UserRoleNotFound(userId, roleId)
    }

    data class Parameters(val userId: Int, val roleId: Int)
}
