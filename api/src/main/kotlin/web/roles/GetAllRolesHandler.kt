package web.roles

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import usecases.role.GetAllRoles

class GetAllRolesHandler(private val getAllRoles: GetAllRoles) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val roles = getAllRoles()
        call.respond(HttpStatusCode.OK, roles)
    }
}