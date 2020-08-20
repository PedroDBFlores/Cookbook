package server.modules

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import model.User
import ports.JWTManager

fun Application.jwtModule(userJwtManager: JWTManager<User>, adminJWTManager: JWTManager<User>) {
    install(Authentication) {
        jwt("user") {
            realm = userJwtManager.realm
            verifier(userJwtManager.verifier)
            validate { call -> userJwtManager.validate(call) }
        }
        jwt("admin") {
            realm = adminJWTManager.realm
            verifier(adminJWTManager.verifier)
            validate { call -> adminJWTManager.validate(call) }
        }
    }
}