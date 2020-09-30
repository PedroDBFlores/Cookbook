package ports

import adapters.authentication.ApplicationRoles
import adapters.authentication.UserPrincipal
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.auth.jwt.*
import model.User

/** Defines the interface for generating/validation/decoding of JWT tokens created by the Cookbook API. */
interface JWTManager {
    /** The JWT's domain */
    val domain: String

    /** The JWT's audience */
    val audience: String

    /** The JWT's realm */
    val realm: String

    /** The allowed roles for this [JWTManager] */
    val allowedRoles: List<ApplicationRoles>

    /** The [JWTVerifier] that's implemented for this manager. Usually validates the provided [ApplicationRoles]. */
    val verifier: JWTVerifier

    /**
     * Generates a token based on an [User]
     * @param user The [User] to generate a JWT token from
     * @return A JWT token
     */
    fun generateToken(user: User): String

    /**
     * Decodes a JWT token
     * @param token The JWT token
     * @return A [DecodedJWT] object
     */
    fun decodeToken(token: String): DecodedJWT?

    /**
     * Validates that the [JWTCredential] domain and audience matches the one from the current manager implementation
     * @param jwtCredential The provided [JWTCredential] from the Ktor application pipeline
     * @return A [UserPrincipal] in case the [JWTCredential] is allowed, null otherwise
     */
    fun validate(jwtCredential: JWTCredential): UserPrincipal?
}