package adapters.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.InternalServerErrorResponse
import model.User

internal object JavalinJWTExtensions {

    private const val CONTEXT_ATTRIBUTE = "jwt"

    internal fun Context.containsJWT(): Boolean = attribute<DecodedJWT>(CONTEXT_ATTRIBUTE) != null

    internal fun Context.addDecodedJWT(jwt: DecodedJWT): Context {
        attribute(CONTEXT_ATTRIBUTE, jwt)
        return this
    }

    internal fun Context.getDecodedJWT(): DecodedJWT {
        if (!containsJWT()) {
            throw InternalServerErrorResponse("No JWT")
        }
        return attribute<DecodedJWT>(CONTEXT_ATTRIBUTE)!!
    }

    internal fun Context.subject(): Int = getDecodedJWT().subject.toInt()

    private fun Context.getTokenFromHeader(): String? {
        return header("Authorization")
            ?.split(" ")?.let {
                if (it.size != 2 || !it[0].contains("Bearer")) {
                    return null
                }
                return it[1]
            }
    }

    internal fun JWTProvider<User>.createHeaderDecodeHandler(): Handler {
        return Handler { context ->
            context.getTokenFromHeader()?.let { token ->
                validateToken(token)?.let { decodedJWT ->
                    context.addDecodedJWT(decodedJWT)
                }
            }
        }
    }
}
