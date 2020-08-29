package usecases.user

import errors.WrongCredentials
import errors.UserNotFound
import model.Credentials
import model.User
import ports.HashingService
import ports.JWTManager
import ports.UserRepository

class LoginUser(
    private val userRepository: UserRepository,
    private val hashingService: HashingService,
    private val jwtManager: JWTManager<User>
) {
    operator fun invoke(parameters: Parameters): String {
        val (credentials) = parameters

        val user = userRepository.find(credentials.userName)
            ?: throw UserNotFound(userName = credentials.userName)

        return if (hashingService.verify(credentials.password, user.passwordHash)) {
            jwtManager.generateToken(user)
        } else {
            throw WrongCredentials()
        }
    }

    data class Parameters(val credentials: Credentials)
}
