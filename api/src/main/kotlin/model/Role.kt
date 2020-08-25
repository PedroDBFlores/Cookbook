package model

import errors.ValidationError

data class Role(
    val id: Int = 0,
    val name: String,
    val code: String,
    val persistent: Boolean = true
) {
    init {
        check(id >= 0) { throw ValidationError("id") }
        check(name.isNotEmpty()) { throw ValidationError("name") }
        check(code.isNotEmpty()) { throw ValidationError("code") }
    }
}
