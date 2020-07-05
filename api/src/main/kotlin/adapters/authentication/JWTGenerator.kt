package adapters.authentication

import com.auth0.jwt.algorithms.Algorithm

interface JWTGenerator<T> {
    fun generate(obj: T, algorithm: Algorithm): String
}
