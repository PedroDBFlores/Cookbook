package usecases.user

import adapters.authentication.JWTProvider
import errors.PasswordMismatchError
import errors.UserNotFound
import model.Credentials
import model.User
import ports.HashingService
import ports.UserRepository

class ValidateUserCredentials(
    private val userRepository: UserRepository,
    private val hashingService: HashingService,
    private val jwtProvider: JWTProvider<User>
) {
    operator fun invoke(credentials: Credentials): String {
        val user = userRepository.find(credentials.username)
            ?: throw UserNotFound(userId = null, userName = credentials.username)
        val passwordHash = hashingService.hash(credentials.password)

        return when (hashingService.verify(passwordHash, user.passwordHash)) {
            true -> jwtProvider.generateToken(user)
            false -> throw PasswordMismatchError()
        }
    }
}
