package web.roles

import errors.RecipeTypeNotFound
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import ports.KtorHandler
import usecases.role.DeleteRole

class DeleteRoleHandler(private val deleteRole: DeleteRole) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        try {
            val roleId = call.parameters.getOrFail<Int>("id")
            require(roleId > 0) { throw BadRequestException("Path param 'id' must be bigger than 0") }

            deleteRole(DeleteRole.Parameters(roleId))
            call.respond(HttpStatusCode.NoContent)
        } catch (ex: RecipeTypeNotFound) {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
