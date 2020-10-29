package usecases.user

import errors.UserNotFound
import ports.UserRepository

class UpdateUser(private val userRepository: UserRepository) {
    operator fun invoke(parameters: Parameters) {
        val (userId, name, oldPassword, newPassword) = parameters

        userRepository.find(userId)?.let {
            userRepository.update(
                user = it.copy(name = name),
                oldPassword = oldPassword,
                newPassword = newPassword
            )
        } ?: throw UserNotFound(id = userId)
    }

    data class Parameters(
        val id: Int,
        val name: String,
        val oldPassword: String? = null,
        val newPassword: String? = null
    )
}
