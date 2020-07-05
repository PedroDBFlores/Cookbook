package model

import errors.ValidationError

data class Credentials(
    val username: String,
    val password: String
) {
    init {
        check(username.isNotEmpty()) { throw ValidationError("username") }
        check(password.isNotEmpty()) { throw ValidationError("password") }
    }
}
