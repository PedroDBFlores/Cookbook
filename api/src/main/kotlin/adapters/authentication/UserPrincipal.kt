package adapters.authentication

import io.ktor.auth.*
import io.ktor.auth.jwt.*

class UserPrincipal(
    val userId: Int,
    val name: String,
    val userName: String,
    val roles: List<String>
) : Principal

fun JWTCredential.toUserPrincipal() = UserPrincipal(
    userId = this.payload.subject.toInt(),
    name = this.payload.getClaim("name").asString(),
    userName = this.payload.getClaim("username").asString(),
    roles = this.payload.getClaim("roles").asList(String::class.java)
)