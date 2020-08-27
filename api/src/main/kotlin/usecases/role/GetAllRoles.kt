package usecases.role

import model.Role
import ports.RoleRepository

class GetAllRoles(private val roleRepository: RoleRepository) {
    operator fun invoke(): List<Role> {
        return roleRepository.getAll()
    }
}