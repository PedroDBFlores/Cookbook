package usecases.user

import errors.UserAlreadyExists
import model.User
import ports.UserRepository

class CreateUser(private val userRepository: UserRepository) {
    operator fun invoke(parameters: Parameters): Int {
        val (name, userName, password) = parameters

        require(userRepository.find(userName = userName) == null) {
            throw UserAlreadyExists(userName)
        }
        return userRepository.create(
            user = User(
                name = name,
                userName = userName
            ),
            userPassword = password
        )
    }

    data class Parameters(
        val name: String,
        val userName: String,
        val password: String
    )
}
