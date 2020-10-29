package adapters.authentication

import io.ktor.auth.*
import io.ktor.auth.jwt.*

/**
 * The [Principal] used throughout the application.
 * @param userId The user's Id
 * @param name The user's name
 * @param userName The user's username
 * @param roles The roles that this user has.
 */
class UserPrincipal(
    val userId: Int,
    val name: String,
    val userName: String,
    val roles: List<String>
) : Principal

fun JWTCredential.toUserPrincipal() = UserPrincipal(
    userId = this.payload.subject.toInt(),
    name = this.payload.getClaim("name").asString(),
    userName = this.payload.getClaim("userName").asString(),
    roles = this.payload.getClaim("roles").asList(String::class.java)
)
