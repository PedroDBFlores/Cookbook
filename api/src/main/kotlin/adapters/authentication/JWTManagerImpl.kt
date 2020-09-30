package adapters.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.auth.jwt.*
import model.User
import org.joda.time.DateTime
import ports.JWTManager

/**
 * The implementation for [JWTManager]
 * @param algorithmSecret The secret to generate/decode a JWT token
 */
class JWTManagerImpl(
    override val domain: String,
    override val audience: String,
    override val realm: String,
    override val allowedRoles: List<ApplicationRoles>,
    algorithmSecret: String
) : JWTManager {
    private val algorithm: Algorithm = Algorithm.HMAC512(algorithmSecret)

    override val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(domain)
        .withAudience(audience)
        .withArrayClaim("roles", *allowedRoles.map { it.name }.toTypedArray())
        .build()

    override fun generateToken(user: User): String {
        val token: JWTCreator.Builder = JWT.create()
            .withIssuer(domain)
            .withAudience(audience)
            .withSubject(user.id.toString())
            .withExpiresAt(DateTime.now().plusDays(3).toDate())
            .withClaim("userName", user.userName)
            .withClaim("name", user.name)
            .withArrayClaim("roles", user.roles?.toTypedArray())
        return token.sign(algorithm)
    }

    override fun validate(jwtCredential: JWTCredential): UserPrincipal? {
        return if (jwtCredential.payload.issuer == domain
            && jwtCredential.payload.audience.contains(audience)
        ) {
            jwtCredential.toUserPrincipal()
        } else null
    }

    override fun decodeToken(token: String): DecodedJWT = JWT.decode(token)

}