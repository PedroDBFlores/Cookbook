package usecases.userroles

import errors.RoleNotFound
import errors.UserNotFound
import ports.RoleRepository
import ports.UserRepository
import ports.UserRolesRepository

class AddRoleToUser(
    private val userRolesRepository: UserRolesRepository,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(parameters: Parameters) {
        val (userId, roleId) = parameters
        require(userRepository.find(userId) != null) {
            throw UserNotFound(id = userId)
        }
        require(roleRepository.find(roleId) != null) {
            throw RoleNotFound(id = roleId)
        }
        userRolesRepository.addRoleToUser(userId, roleId)
    }

    data class Parameters(val userId: Int, val roleId: Int)
}
