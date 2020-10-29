package web.user

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import ports.KtorHandler
import usecases.user.DeleteUser

class DeleteUserHandler(
    private val deleteUser: DeleteUser
) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val userId = call.parameters.getOrFail<Int>("id")
        require(userId > 0) { throw BadRequestException("Path param 'id' must be bigger than 0") }

        deleteUser(DeleteUser.Parameters(userId))
        call.respond(HttpStatusCode.NoContent)
    }
}
