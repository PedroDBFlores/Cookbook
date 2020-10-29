package usecases.role

import errors.RoleAlreadyExists
import model.Role
import ports.RoleRepository

class CreateRole(private val roleRepository: RoleRepository) {
    operator fun invoke(parameters: Parameters): Int {
        val (name, code) = parameters

        require(roleRepository.find(code) == null) {
            throw RoleAlreadyExists(code)
        }
        return roleRepository.create(Role(name = name, code = code))
    }

    data class Parameters(val name: String, val code: String)
}
