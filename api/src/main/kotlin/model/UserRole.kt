package model

import errors.ValidationError

data class UserRole(
    val userId: Int,
    val roleId: Int
) {
    init {
        check(userId >= 0) { throw ValidationError("userId") }
        check(roleId >= 0) { throw ValidationError("roleId") }
    }
}
