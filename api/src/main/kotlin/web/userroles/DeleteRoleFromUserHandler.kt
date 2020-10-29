package web.userroles

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import ports.KtorHandler
import usecases.userroles.DeleteRoleFromUser

class DeleteRoleFromUserHandler(private val deleteRoleFromUser: DeleteRoleFromUser) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val userId = call.parameters.getOrFail<Int>("userId")
        require(userId > 0) { throw BadRequestException("Path param 'userId' must be bigger than 0") }
        val roleId = call.parameters.getOrFail<Int>("roleId")
        require(roleId > 0) { throw BadRequestException("Path param 'roleId' must be bigger than 0") }

        deleteRoleFromUser(DeleteRoleFromUser.Parameters(userId, roleId))
        call.respond(HttpStatusCode.NoContent)
    }
}
