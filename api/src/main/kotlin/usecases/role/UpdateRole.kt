package usecases.role

import errors.OperationNotAllowed
import errors.RoleNotFound
import ports.RoleRepository

class UpdateRole(private val roleRepository: RoleRepository) {
    operator fun invoke(parameters: Parameters) {
        val (id, name, code) = parameters

        roleRepository.find(id)?.let {
            require(!it.persistent) {
                throw OperationNotAllowed("Cannot update a persistent role")
            }
            roleRepository.update(it.copy(name = name, code = code))
        } ?: throw RoleNotFound(id = id)
    }

    data class Parameters(val id: Int, val name: String, val code: String)
}
