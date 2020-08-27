package model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import errors.ValidationError

data class User(
    val id: Int = 0,
    val name: String,
    val userName: String,
    @JsonIgnore val passwordHash: String = "",
    @JsonProperty("roles", required = false, defaultValue = "[]") val roles: List<String>? = emptyList()
) {
    init {
        check(id >= 0) { throw ValidationError("id") }
        check(name.isNotEmpty()) { throw ValidationError("name") }
        check(userName.isNotEmpty()) { throw ValidationError("username") }
    }
}

