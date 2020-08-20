package usecases.user

import errors.UserAlreadyExists
import model.User
import ports.UserRepository

class CreateUser(private val userRepository: UserRepository) {
    operator fun invoke(user: User, userPassword: String): Int {
        require(userRepository.find(userName = user.userName) == null) {
            throw UserAlreadyExists()
        }
        return userRepository.create(user = user, userPassword = userPassword)
    }
}
