package usecases.user

import errors.UserNotFound
import ports.HashingService
import ports.UserRepository

class ValidateUserCredentials(private val userRepository: UserRepository, private val hashingService: HashingService) {
    operator fun invoke(parameters: Parameters): Boolean {
        val user = userRepository.find(parameters.username)
            ?: throw UserNotFound(userId = null, userName = parameters.username)
        val passwordHash = hashingService.hash(parameters.password)
        return hashingService.verify(passwordHash, user.passwordHash)
    }

    data class Parameters(val username: String, val password: String)
}