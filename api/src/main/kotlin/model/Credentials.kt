package model

import errors.ValidationError

/**
 * Represents the credentials provided for obtaining a JWT token for the API
 * @param username The user's username
 * @param password The user's password
 */
data class Credentials(
    val username: String,
    val password: String
) {
    init {
        check(username.isNotEmpty()) { throw ValidationError("username") }
        check(password.isNotEmpty()) { throw ValidationError("password") }
    }
}
