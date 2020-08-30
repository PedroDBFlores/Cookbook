package web.roles

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.role.CreateRole

class CreateRoleHandler(private val createRole: CreateRole) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val createRoleRepresenter = call.receiveOrThrow<CreateRoleRepresenter>()
        val roleId = createRole(createRoleRepresenter.toParameters())
        call.respond(HttpStatusCode.Created, CreateResult(roleId))
    }

    private data class CreateRoleRepresenter(
        val name: String,
        val code: String
    ) {
        init {
            check(name.isNotEmpty()) { "Field 'name' must not be empty" }
            check(code.isNotEmpty()) { "Field 'code' must not be empty" }
        }

        fun toParameters() = CreateRole.Parameters(name, code)
    }
}