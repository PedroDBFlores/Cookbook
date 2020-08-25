package usecases.user

import errors.UserNotFound
import ports.UserRepository

class DeleteUser(private val repository: UserRepository) {
    operator fun invoke(parameters: Parameters) {
        val (userId) = parameters

        val deleted = repository.delete(userId)
        if (!deleted) throw UserNotFound(userId)
    }

    data class Parameters(val userId: Int)
}
