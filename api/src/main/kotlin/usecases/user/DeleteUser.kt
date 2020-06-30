package usecases.user

import errors.UserNotFound
import ports.UserRepository

class DeleteUser(private val repository: UserRepository) {
    operator fun invoke(parameters: Parameters) {
        when (repository.delete(parameters.userId)) {
            false -> throw UserNotFound(parameters.userId)
        }
    }

    data class Parameters(val userId: Int)
}