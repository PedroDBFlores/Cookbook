package adapters.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import model.User

object HMAC512Provider {
    private val generator: JWTGenerator<User> = object : JWTGenerator<User> {
        override fun generate(obj: User, algorithm: Algorithm): String {
            val token: JWTCreator.Builder = JWT.create()
                .withAudience("cookbook")
                .withSubject(obj.id.toString())
                .withClaim("username", obj.username)
                .withClaim("name", obj.name)
                .withArrayClaim("roles", obj.roles?.toTypedArray())
            return token.sign(algorithm)
        }
    }

    fun provide(algorithmSecret: String): JWTProvider<User> {
        val algorithm = Algorithm.HMAC512(algorithmSecret)
        val verifier: JWTVerifier = JWT.require(algorithm).build()

        return JWTProvider(algorithm, generator, verifier)
    }
}
