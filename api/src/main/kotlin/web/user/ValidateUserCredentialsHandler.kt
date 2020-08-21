package web.user

import errors.PasswordMismatchError
import errors.UserNotFound
import errors.ValidationError
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.Credentials
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.user.ValidateUserCredentials

class ValidateUserCredentialsHandler(private val validateUserCredentials: ValidateUserCredentials) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        try {
            val credentialsRepresenter = call.receiveOrThrow<CredentialsRepresenter>()

            val token = validateUserCredentials.invoke(credentialsRepresenter.toCredentials())
            call.respond(HttpStatusCode.OK, token)
        } catch (notFoundEx: UserNotFound) {
            call.respond(HttpStatusCode.Forbidden)
        } catch (passMismatchEx: PasswordMismatchError) {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    private data class CredentialsRepresenter(
        val username: String,
        val password: String
    ) {
        init {
            check(username.isNotEmpty()) { throw ValidationError("username") }
            check(password.isNotEmpty()) { throw ValidationError("password") }
        }

        fun toCredentials() = Credentials(username = username, password = password)
    }
}
