package web.user

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.user.UpdateUser

class UpdateUserHandler(private val updateUser: UpdateUser) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val updateUserRepresenter = call.receiveOrThrow<UpdateUserRepresenter>()
        updateUser.invoke(updateUserRepresenter.toParameters())
        call.respond(HttpStatusCode.OK)
    }

    private data class UpdateUserRepresenter(
        val id: Int,
        val name: String,
        val oldPassword: String?,
        val newPassword: String?
    ) {
        init {
            check(id > 0) { "Field 'id' must be bigger than zero" }
            newPassword?.let { newPassword ->
                check(newPassword.isEmpty() || (newPassword.isNotEmpty() && oldPassword?.isNotEmpty() ?: false)) {
                    throw BadRequestException("You must have both passwords provided.")
                }
            }
        }

        fun toParameters() = UpdateUser.Parameters(
            id = id,
            name = name,
            oldPassword = oldPassword,
            newPassword = newPassword
        )
    }
}
