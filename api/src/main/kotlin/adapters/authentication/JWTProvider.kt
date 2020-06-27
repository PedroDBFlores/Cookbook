package adapters.authentication

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier

class JWTProvider(
    private val algorithm: Algorithm,
    private val jwtGenerator: JWTGenerator,
    private val jwtVerifier: JWTVerifier
) {
    fun generateToken(obj: Any): String = jwtGenerator.generate(obj, algorithm)

    fun validateToken(token: String): DecodedJWT? {
        return try {
            jwtVerifier.verify(token)
        } catch (ex: JWTVerificationException) {
            null
        }
    }
}

