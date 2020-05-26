package pt.pedro.cookbook.server.modules

import com.papsign.ktor.openapigen.openAPIGen
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import org.kodein.di.Kodein
import pt.pedro.cookbook.handler.RecipeTypeHandler
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

@Suppress("unused") // Referenced in application.conf
fun Application.routingModule(testing: Boolean = false) {
    val kodein = kodein()

    install(Routing) {

        get("/openapi.json") {
            call.respond(openAPIGen.api.serialize())
        }
        get("/") {
            call.respondRedirect("/swagger-ui/index.html", true)
        }

        val recipeTypeHandler by kodein.instance<RecipeTypeHandler>()
        route("/api/recipetype") {
            get("{id}") { recipeTypeHandler.get(call) }
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }

        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
