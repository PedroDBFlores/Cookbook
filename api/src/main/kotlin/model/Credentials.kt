package model

import errors.ValidationError

/**
 * Represents the credentials provided for obtaining a JWT token for the API
 * @param userName The user's username
 * @param password The user's password
 */
data class Credentials(
    val userName: String,
    val password: String
) {
    init {
        check(userName.isNotEmpty()) { throw ValidationError("username") }
        check(password.isNotEmpty()) { throw ValidationError("password") }
    }
}
