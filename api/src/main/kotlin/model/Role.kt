package model

import errors.ValidationError

data class Role(
    val id: Int,
    val name: String,
    val code: String,
    val persistent: Boolean
) {
    init {
        check(id >= 0) { throw ValidationError("id") }
        check(name.isNotEmpty()) { throw ValidationError("name") }
        check(code.isNotEmpty()) { throw ValidationError("code") }
    }
}
