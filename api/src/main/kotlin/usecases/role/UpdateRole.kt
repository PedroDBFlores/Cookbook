package usecases.role

import errors.OperationNotAllowed
import errors.RoleNotFound
import model.Role
import ports.RoleRepository

class UpdateRole(private val roleRepository: RoleRepository) {
    operator fun invoke(parameters: Parameters) {
        val (role) = parameters

        roleRepository.find(role.id)?.let {
            require(!it.persistent){
                throw OperationNotAllowed("Cannot update a persistent role")
            }
        } ?: throw RoleNotFound(id = role.id)

        roleRepository.update(role)
    }

    data class Parameters(val role: Role)
}