package web.userroles

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import ports.KtorHandler
import usecases.userroles.GetUserRoles

class GetUserRolesHandler(private val getUserRoles: GetUserRoles) : KtorHandler{
    override suspend fun handle(call: ApplicationCall) {
        val id = call.parameters.getOrFail<Int>("id")

        call.respond(HttpStatusCode.OK, getUserRoles(GetUserRoles.Parameters(id)))
    }
}
