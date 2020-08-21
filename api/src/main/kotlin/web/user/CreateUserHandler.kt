package web.user

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import model.User
import ports.KtorHandler
import server.extensions.validateReceivedBody
import usecases.user.CreateUser

class CreateUserHandler(private val createUser: CreateUser) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val createUserRepresenter = call.validateReceivedBody<CreateUserRepresenter> { }
        val userId = createUser(createUserRepresenter.toUser(), createUserRepresenter.password)
        call.respond(HttpStatusCode.Created, CreateResult(userId))
    }

    private data class CreateUserRepresenter(
        val name: String,
        val userName: String,
        val password: String,
    ) {
        fun toUser() = User(
            id = 0,
            name = name,
            userName = userName
        )
    }
}