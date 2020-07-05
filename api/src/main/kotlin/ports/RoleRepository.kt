package ports

import model.Role

interface RoleRepository {
    fun find(id: Int): Role?
    fun find(code: String): Role?
    fun getAll(): List<Role>
    fun create(role: Role): Int
    fun update(role: Role)
    fun delete(id: Int): Boolean
}
