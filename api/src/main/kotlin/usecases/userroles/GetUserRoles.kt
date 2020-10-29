package usecases.userroles

import errors.UserNotFound
import model.UserRole
import ports.UserRepository
import ports.UserRolesRepository

class GetUserRoles(
    private val userRolesRepository: UserRolesRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(parameters: Parameters): List<UserRole> {
        val (userId) = parameters
        require(userRepository.find(userId) != null) {
            throw UserNotFound(id = userId)
        }

        return userRolesRepository.getRolesForUser(userId)
    }

    data class Parameters(val userId: Int)
}
