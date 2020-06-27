package adapters.authentication

import com.auth0.jwt.algorithms.Algorithm

interface JWTGenerator {
    fun generate(obj: Any, algorithm: Algorithm): String
}