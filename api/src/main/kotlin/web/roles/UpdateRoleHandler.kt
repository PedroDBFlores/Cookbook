package web.roles

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.role.UpdateRole

class UpdateRoleHandler(private val updateRole: UpdateRole) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val updateRoleRepresenter = call.receiveOrThrow<UpdateRoleRepresenter>()
        updateRole(updateRoleRepresenter.toParameters())
        call.respond(HttpStatusCode.OK)
    }

    private data class UpdateRoleRepresenter(
        val id: Int,
        val name: String,
        val code: String
    ) {
        init {
            check(id > 0) { "Field 'id' must be bigger than zero" }
            check(name.isNotEmpty()) { "Field 'name' must not be empty" }
            check(code.isNotEmpty()) { "Field 'code' must not be empty" }
        }

        fun toParameters() = UpdateRole.Parameters(id, name, code)
    }
}
