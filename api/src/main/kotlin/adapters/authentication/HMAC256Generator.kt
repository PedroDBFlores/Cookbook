package adapters.authentication

import com.auth0.jwt.algorithms.Algorithm

class HMAC256Generator : JWTGenerator {
    override fun generate(obj: Any, algorithm: Algorithm): String {
        TODO("Not yet implemented")
    }
}