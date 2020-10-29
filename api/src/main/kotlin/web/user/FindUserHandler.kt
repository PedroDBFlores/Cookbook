package web.user

import errors.UserNotFound
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import ports.KtorHandler
import usecases.user.FindUser

class FindUserHandler(private val findUser: FindUser) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        try {
            val userId = call.parameters.getOrFail<Int>("id")
            require(userId > 0) { throw BadRequestException("Path param 'id' must be bigger than 0") }

            val user = findUser(FindUser.Parameters(userId = userId))
            call.respond(HttpStatusCode.OK, user)
        } catch (unfException: UserNotFound) {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
