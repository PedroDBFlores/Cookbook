package adapters.authentication

import adapters.authentication.JavalinJWTExtensions.containsJWT
import adapters.authentication.JavalinJWTExtensions.getDecodedJWT
import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus

class CookbookAccessManager(
    private val userRolesClaim: String,
    private val rolesMapping: Map<String, Role>,
    private val defaultRole: Role
) : AccessManager {
    override fun manage(handler: Handler, ctx: Context, allowedRoles: MutableSet<Role>) {
        val userRoles = extractRoles(ctx)

        if (allowedRoles.intersect(userRoles).isNotEmpty()) {
            handler.handle(ctx)
        } else {
            ctx.status(HttpStatus.FORBIDDEN_403).result("You don't have permission to use this resource")
        }
    }

    private fun extractRoles(context: Context): List<Role> {
        if (!context.containsJWT()) {
            return listOf(defaultRole)
        }
        val decodedJWT = context.getDecodedJWT()
        val userRolesClaim = decodedJWT.getClaim(userRolesClaim)
        val userRoles = userRolesClaim.asList(String::class.java)
        return rolesMapping.filter { role -> userRoles.contains(role.key) }
            .map { rolesMapping -> rolesMapping.value }

    }
}