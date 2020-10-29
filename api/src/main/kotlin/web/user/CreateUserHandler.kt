package web.user

import adapters.authentication.ApplicationRoles
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.role.FindRole
import usecases.user.CreateUser
import usecases.userroles.AddRoleToUser

class CreateUserHandler(
    private val createUser: CreateUser,
    private val findRole: FindRole,
    private val addRoleToUser: AddRoleToUser
) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val parameters = call.receiveOrThrow<CreateUserRepresenter>()
            .toParameters()
        val userId = createUser(parameters)
        val roleId = findRole(FindRole.Parameters(ApplicationRoles.USER.name)).id
        addRoleToUser(AddRoleToUser.Parameters(userId, roleId))
        call.respond(HttpStatusCode.Created, CreateResult(userId))
    }

    private data class CreateUserRepresenter(
        val name: String,
        val userName: String,
        val password: String,
    ) {
        init {
            check(name.isNotBlank()) { "Field 'name' must not be empty" }
            check(userName.isNotBlank()) { "Field 'userName' must not be empty" }
            check(password.isNotBlank()) { "Field 'password' must not be empty" }
        }

        fun toParameters() = CreateUser.Parameters(name, userName, password)
    }
}
