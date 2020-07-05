package web.user

import errors.PasswordMismatchError
import errors.UserNotFound
import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import model.Credentials
import org.eclipse.jetty.http.HttpStatus
import usecases.user.ValidateUserCredentials
import utils.convertToJSON
import utils.removeJSONProperties

internal class ValidateUserCredentialsHandlerTest : DescribeSpec({
    val credentials = Credentials(username = "username", password = "password")
    val jsonBody = convertToJSON(credentials)

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        validateUserCredentials: ValidateUserCredentials,
        jsonBody: String
    ): Response {
        val app = Javalin.create().post("/api/user/validate", ValidateUserCredentialsHandler(validateUserCredentials))
            .start(9000)

        try {
            return Given {
                contentType(ContentType.JSON)
                body(jsonBody)
            } When {
                post("api/user/validate")
            } Extract {
                response()
            }
        } finally {
            app.stop()
        }
    }

    describe("Validate user credentials handler") {
        it("returns 200 with a JWT if the user is authorized") {
            val validateUserCredentials = mockk<ValidateUserCredentials> {
                every {
                    this@mockk(
                        Credentials(
                            username = credentials.username,
                            password = credentials.password
                        )
                    )
                } returns "A_VALID_TOKEN"
            }

            val response = executeRequest(
                validateUserCredentials = validateUserCredentials,
                jsonBody = jsonBody
            )

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                body.asString().shouldBe("A_VALID_TOKEN")
            }
            verify(exactly = 1) {
                validateUserCredentials.invoke(
                    Credentials(
                        username = credentials.username,
                        password = credentials.password
                    )
                )
            }
        }

        it("returns 404 if the user is not found") {
            val validateUserCredentials = mockk<ValidateUserCredentials> {
                every { this@mockk(any()) } throws UserNotFound()
            }

            val response = executeRequest(
                validateUserCredentials = validateUserCredentials,
                jsonBody = jsonBody
            )

            with(response) {
                statusCode.shouldBe(HttpStatus.NOT_FOUND_404)
            }
        }

        it("returns 401 if the user credentials aren't valid") {
            val validateUserCredentials = mockk<ValidateUserCredentials> {
                every { this@mockk(any()) } throws PasswordMismatchError()
            }

            val response = executeRequest(
                validateUserCredentials = validateUserCredentials,
                jsonBody = jsonBody
            )

            with(response) {
                statusCode.shouldBe(HttpStatus.UNAUTHORIZED_401)
            }
        }

        arrayOf(
            row(
                "",
                "when there is no body"
            ),
            row(
                """{"non":"conformant"}""",
                "an invalid body is provided"
            ),
            row(
                removeJSONProperties(credentials, "username"),
                "when the username property is missing"
            ),
            row(
                removeJSONProperties(credentials, "password"),
                "when the password property is missing"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 $description") {
                val validateUserCredentials = mockk<ValidateUserCredentials>(relaxed = true)

                val response = executeRequest(
                    validateUserCredentials = validateUserCredentials,
                    jsonBody = jsonBody
                )

                with(response) {
                    statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                    body.asString().shouldContain("Couldn't deserialize body")
                }
            }
        }
    }
})
