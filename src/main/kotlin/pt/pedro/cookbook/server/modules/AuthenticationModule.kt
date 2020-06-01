package pt.pedro.cookbook.server.modules

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt

@Suppress("unused") // Referenced in application.conf
fun Application.authenticationModule(testing: Boolean = false) {
    install(Authentication) {
        jwt {

        }
    }
}
