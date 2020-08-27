package usecases.userroles

import ports.UserRolesRepository

class DeleteRoleFromUser(private val userRolesRepository: UserRolesRepository) {
    operator fun invoke(parameters: Parameters) {

    }

    data class Parameters(val userId: Int, val roleId: Int)
}