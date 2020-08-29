package web.userroles

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.userroles.AddRoleToUser

class AddRoleToUserHandler(private val addRoleToUser: AddRoleToUser) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val addRoleToUserRepresenter = call.receiveOrThrow<AddRoleToUserRepresenter>()

        addRoleToUser(addRoleToUserRepresenter.toParameters())
        call.respond(HttpStatusCode.Created)
    }

    private data class AddRoleToUserRepresenter(
        val userId: Int,
        val roleId: Int
    ) {
        init {
            check(userId > 0) { "Field 'userId' must be bigger than zero" }
            check(roleId > 0) { "Field 'roleId' must be bigger than zero" }
        }

        fun toParameters() = AddRoleToUser.Parameters(userId, roleId)
    }
}