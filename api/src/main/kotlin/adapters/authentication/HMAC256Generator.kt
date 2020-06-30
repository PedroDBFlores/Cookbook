package adapters.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import model.User


class HMAC256Generator : JWTGenerator<User> {
    override fun generate(obj: User, algorithm: Algorithm): String {
        val token: JWTCreator.Builder = JWT.create()
            .withClaim("name", obj.name)
            .withArrayClaim("roles", obj.roles?.toTypedArray())
        return token.sign(algorithm)
    }
}