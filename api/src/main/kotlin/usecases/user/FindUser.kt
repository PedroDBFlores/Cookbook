package usecases.user

import errors.UserNotFound
import model.User
import ports.UserRepository

class FindUser(private val userRepository: UserRepository) {
    operator fun invoke(parameters: Parameters): User {
        val (userId) = parameters

        return userRepository.find(userId) ?: throw UserNotFound(userId)
    }

    data class Parameters(val userId: Int)
}
