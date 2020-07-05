package web.user

import errors.PasswordMismatchError
import errors.UserNotFound
import io.javalin.http.Context
import io.javalin.http.Handler
import model.Credentials
import org.eclipse.jetty.http.HttpStatus
import usecases.user.ValidateUserCredentials

class ValidateUserCredentialsHandler(private val validateUserCredentials: ValidateUserCredentials) : Handler {
    override fun handle(ctx: Context) {
        try {
            val credentials = ctx.body<Credentials>()

            val token = validateUserCredentials.invoke(credentials)
            ctx.status(HttpStatus.OK_200).result(token)
        } catch (notFoundEx: UserNotFound) {
            ctx.status(HttpStatus.NOT_FOUND_404)
        } catch (passMismatchEx: PasswordMismatchError) {
            ctx.status(HttpStatus.UNAUTHORIZED_401)
        }
    }
}
