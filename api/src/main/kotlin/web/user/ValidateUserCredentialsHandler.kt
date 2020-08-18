package web.user

import errors.PasswordMismatchError
import errors.UserNotFound
import errors.ValidationError
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.HttpMethod
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import io.swagger.v3.oas.annotations.media.Schema
import model.Credentials
import org.eclipse.jetty.http.HttpStatus
import usecases.user.ValidateUserCredentials

class ValidateUserCredentialsHandler(private val validateUserCredentials: ValidateUserCredentials) : Handler {

    @OpenApi(
        summary = "Validate user credentials",
        description = "Validates the user's credentials and returns a JWT token",
        method = HttpMethod.POST,
        requestBody = OpenApiRequestBody(
            content = [
                OpenApiContent(from = CredentialsRepresenter::class)
            ],
            required = true
        ),
        responses = [
            OpenApiResponse(
                status = "200",
                description = "The user's JWT token",
                content = [OpenApiContent(from = String::class)]
            )
        ]
    )
    override fun handle(ctx: Context) {
        try {
            val credentialsRepresenter = ctx.body<CredentialsRepresenter>()

            val token = validateUserCredentials.invoke(credentialsRepresenter.toCredentials())
            ctx.status(HttpStatus.OK_200).result(token)
        } catch (notFoundEx: UserNotFound) {
            ctx.status(HttpStatus.NOT_FOUND_404)
        } catch (passMismatchEx: PasswordMismatchError) {
            ctx.status(HttpStatus.UNAUTHORIZED_401)
        }
    }
}

private data class CredentialsRepresenter(
    @Schema(required = true, name = "username", description = "The user's username", example = "userone")
    val username: String,
    @Schema(required = true, name = "password", description = "The user's password", example = "userpassword")
    val password: String
) {
    init {
        check(username.isNotEmpty()) { throw ValidationError("username") }
        check(password.isNotEmpty()) { throw ValidationError("password") }
    }

    fun toCredentials() = Credentials(username = username, password = password)
}
