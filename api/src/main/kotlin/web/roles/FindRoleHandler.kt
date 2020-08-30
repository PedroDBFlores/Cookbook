package web.roles

import errors.RecipeTypeNotFound
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*
import ports.KtorHandler
import usecases.role.FindRole

class FindRoleHandler(private val findRole: FindRole) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        try {
            val roleCode = call.parameters.getOrFail<String>("code")
            require(roleCode.isNotEmpty()) { throw BadRequestException("Path param 'code' must not be empty") }

            val role = findRole(FindRole.Parameters(roleCode))
            call.respond(HttpStatusCode.OK, role)
        } catch (ex: RecipeTypeNotFound) {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}