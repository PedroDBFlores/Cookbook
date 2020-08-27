package usecases.role

import errors.OperationNotAllowed
import errors.RoleNotFound
import ports.RoleRepository

class DeleteRole(private val roleRepository: RoleRepository) {
    operator fun invoke(parameters: Parameters): Boolean {
        val (roleId) = parameters

        val currentRole = roleRepository.find(roleId)
        currentRole?.let { role ->
            require(!role.persistent) {
                throw OperationNotAllowed("Cannot delete a persistent role")

            }
        } ?: throw RoleNotFound(id = roleId)

        return roleRepository.delete(roleId)
    }

    data class Parameters(val roleId: Int)
}