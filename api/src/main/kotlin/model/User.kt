package model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("username") val userName: String,
    @JsonIgnore val passwordHash: String = "",
    @JsonProperty("roles", required = false, defaultValue = "[]") val roles: List<String>? = emptyList()
)