package web.user

import io.javalin.http.Context
import io.javalin.http.Handler
import usecases.user.ValidateUserCredentials

class ValidateUserCredentialsHandler(private val validateUserCredentials: ValidateUserCredentials) : Handler {
    override fun handle(ctx: Context) {
        TODO("Not yet implemented")
    }
}