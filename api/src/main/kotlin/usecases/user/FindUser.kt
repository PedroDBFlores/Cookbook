package usecases.user

import errors.UserNotFound
import model.User
import ports.UserRepository

class FindUser(private val userRepository: UserRepository) {
    operator fun invoke(parameters: Parameters): User {
        return userRepository.find(parameters.userId) ?: throw UserNotFound(parameters.userId)
    }

    data class Parameters(val userId: Int)
}
