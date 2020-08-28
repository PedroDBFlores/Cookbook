package usecases.user

import errors.UserAlreadyExists
import model.User
import ports.UserRepository

class CreateUser(private val userRepository: UserRepository) {
    operator fun invoke(parameters: Parameters): Int {
        val (user, userPassword) = parameters

        // TODO: Link the ROLE to every one
        require(userRepository.find(userName = user.userName) == null) {
            throw UserAlreadyExists(user.userName)
        }
        return userRepository.create(user = user, userPassword = userPassword)
    }

    data class Parameters(val user: User, val userPassword: String)
}
