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
import io.ktor.routing.*
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import pt.pedro.cookbook.domain.DatabaseManager
import pt.pedro.cookbook.handler.RecipeTypeHandler

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

        route("/api/recipetype") {
            val handler by kodein.instance<RecipeTypeHandler>()

            get("{id}") { handler.get(call) }
            get { handler.getAll(call) }
            post { handler.create(call) }
            put { handler.update(call) }
            delete("{id}") { handler.delete(call) }
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
