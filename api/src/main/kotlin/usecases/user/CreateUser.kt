package usecases.user

import model.User
import ports.UserRepository

class CreateUser(private val userRepository: UserRepository) {
    operator fun invoke(user: User, userPassword: String): Int {
        return userRepository.create(user = user, userPassword = userPassword)
    }
}