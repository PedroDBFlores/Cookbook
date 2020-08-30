package usecases.role

import errors.OperationNotAllowed
import errors.RoleNotFound
import ports.RoleRepository

class DeleteRole(private val roleRepository: RoleRepository) {
    operator fun invoke(parameters: Parameters) {
        val (roleId) = parameters

        val currentRole = roleRepository.find(roleId)
        currentRole?.let { role ->
            require(!role.persistent) {
                throw OperationNotAllowed("Cannot delete a persistent role")
            }
            roleRepository.delete(roleId)
        } ?: throw RoleNotFound(id = roleId)
    }

    data class Parameters(val roleId: Int)
}