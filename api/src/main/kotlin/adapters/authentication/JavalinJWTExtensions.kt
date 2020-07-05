package adapters.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.InternalServerErrorResponse
import model.User

internal object JavalinJWTExtensions {

    private const val CONTEXT_ATTRIBUTE = "jwt"
    private const val COOKIE_KEY = "jwt"

    internal fun Context.containsJWT(): Boolean = attribute<DecodedJWT>(CONTEXT_ATTRIBUTE) != null

    internal fun Context.addDecodedJWT(jwt: DecodedJWT): Context {
        attribute(CONTEXT_ATTRIBUTE, jwt)
        return this
    }

    internal fun Context.getTokenFromCookie(): String? = cookie(COOKIE_KEY)

    internal fun Context.addTokenToCookie(token: String): Context = cookie(COOKIE_KEY, token)

    internal fun Context.getDecodedJWT(): DecodedJWT {
        if (!containsJWT()) {
            throw InternalServerErrorResponse("No JWT")
        }
        return attribute<DecodedJWT>(CONTEXT_ATTRIBUTE)!!
    }

    internal fun Context.getTokenFromHeader(): String? {
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

    internal fun JWTProvider<User>.createCookieDecodeHandler(): Handler {
        return Handler { context ->
            context.getTokenFromCookie()?.let { token ->
                validateToken(token)?.let { decodedJWT ->
                    context.addDecodedJWT(decodedJWT)
                }
            }
        }
    }
}
