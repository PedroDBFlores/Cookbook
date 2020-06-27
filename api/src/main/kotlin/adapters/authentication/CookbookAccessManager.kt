package adapters.authentication

import adapters.authentication.JavalinJWTExtensions.containsJWT
import adapters.authentication.JavalinJWTExtensions.getDecodedJWT
import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler

class CookbookAccessManager(
    private val userRoleClaim: String,
    private val rolesMapping: Map<String, Role>,
    private val defaultRole: Role
) : AccessManager {
    override fun manage(handler: Handler, ctx: Context, permittedRoles: MutableSet<Role>) {
        val role = extractRole(ctx)

        if (permittedRoles.contains(role)) {
            handler.handle(ctx)
        }
    }

    private fun extractRole(context: Context): Role {
        if (!context.containsJWT()) {
            return defaultRole
        }
        val decodedJWT = context.getDecodedJWT()
        val userRole = decodedJWT.getClaim(userRoleClaim)?.toString()
        return userRole?.let {
            rolesMapping[userRole]
        } ?: defaultRole
    }
}