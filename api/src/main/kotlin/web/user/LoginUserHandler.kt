package web.user

import errors.WrongCredentials
import errors.UserNotFound
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.Credentials
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.user.LoginUser

class LoginUserHandler(private val loginUser: LoginUser) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        try {
            val credentialsRepresenter = call.receiveOrThrow<CredentialsRepresenter>()
            val token = loginUser.invoke(LoginUser.Parameters(credentialsRepresenter.toCredentials()))
            call.respond(HttpStatusCode.OK, mapOf("token" to token))
        } catch (notFoundEx: UserNotFound) {
            call.respond(HttpStatusCode.Forbidden)
        } catch (passMismatchEx: WrongCredentials) {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    private data class CredentialsRepresenter(
        val userName: String,
        val password: String
    ) {
        fun toCredentials() = Credentials(userName = userName, password = password)
    }
}
