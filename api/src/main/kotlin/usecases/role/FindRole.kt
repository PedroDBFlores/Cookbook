package usecases.role

import errors.RoleNotFound
import model.Role
import ports.RoleRepository

class FindRole(private val roleRepository: RoleRepository) {
    operator fun invoke(parameters: Parameters): Role {
        val (code) = parameters

        return roleRepository.find(code) ?: throw RoleNotFound(code)
    }

    data class Parameters(val code: String)
}