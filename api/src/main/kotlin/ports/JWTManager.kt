package ports

import adapters.authentication.ApplicationRoles
import adapters.authentication.UserPrincipal
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.auth.jwt.*

interface JWTManager<T> {
    val domain: String
    val audience: String
    val realm: String
    val allowedRoles: List<ApplicationRoles>

    val verifier: JWTVerifier

    fun generateToken(obj: T): String
    fun decodeToken(token: String): DecodedJWT?
    fun validate(jwtCredential: JWTCredential): UserPrincipal?
}