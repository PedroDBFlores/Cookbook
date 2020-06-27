package ports

import model.User

interface UserRepository {
    fun find(id: Int): User?
    fun find(userName: String): User?
    fun create(user: User, userPassword: String): Int
    fun update(user: User, oldPassword: String? = null, newPassword: String? = null)
    fun delete(id: Int): Boolean
}

class PasswordMismatchError : Exception("The provided password doesn't match the one in the database")

