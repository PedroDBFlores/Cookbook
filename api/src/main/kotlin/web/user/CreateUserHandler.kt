package web.user

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.CreateResult
import model.User
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.user.CreateUser

class CreateUserHandler(private val createUser: CreateUser) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val createUserRepresenter = call.receiveOrThrow<CreateUserRepresenter>()
        val userId = createUser(
            CreateUser.Parameters(
                createUserRepresenter.asUser(),
                createUserRepresenter.password
            )
        )
        call.respond(HttpStatusCode.Created, CreateResult(userId))
    }

    private data class CreateUserRepresenter(
        val name: String,
        val userName: String,
        val password: String,
    ) {
        private val user = User(
            id = 0,
            name = name,
            userName = userName
        )

        init {
            check(password.isNotEmpty()) { "Field 'password' must not be empty" }
        }

        fun asUser() = user
    }
}