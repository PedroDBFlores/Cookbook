package server.modules

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import model.User
import ports.JWTManager

fun Application.jwtModule(userJwtManager: JWTManager, adminJWTManager: JWTManager) {
    install(Authentication) {
        jwt("user") {
            realm = userJwtManager.realm
            verifier(userJwtManager.verifier)
            validate { call -> userJwtManager.validate(call) }
            challenge { defaultScheme, realm -> handleChallenge(defaultScheme, realm, call) }

        }
        jwt("admin") {
            realm = adminJWTManager.realm
            verifier(adminJWTManager.verifier)
            validate { call -> adminJWTManager.validate(call) }
            challenge { defaultScheme, realm -> handleChallenge(defaultScheme, realm, call) }
        }
    }
}

private suspend fun handleChallenge(defaultScheme: String, realm: String, call: ApplicationCall) {
    call.response.headers.append("WWW-Authenticate", "Bearer realm=$realm")
    call.respond(
        HttpStatusCode.Unauthorized,
        ResponseError(
            HttpStatusCode.Unauthorized.value.toString(),
            "Scheme $defaultScheme isn't authorized to use the realm $realm"
        )
    )
}