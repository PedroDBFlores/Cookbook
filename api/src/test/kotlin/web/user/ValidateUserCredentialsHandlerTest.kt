package web.user

import errors.PasswordMismatchError
import errors.UserNotFound
import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
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

class ValidateUserCredentialsHandlerTest : DescribeSpec({
    var app: Javalin? = null
    val credentials = object {
        val username = "username"
        val password = "password"
    }
    val jsonBody = convertToJSON(credentials)

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        validateUserCredentials: ValidateUserCredentials,
        jsonBody: String
    ): Response {
        app = Javalin.create().post("/api/user/validate", ValidateUserCredentialsHandler(validateUserCredentials))
            .start(9000)

        return Given {
            contentType(ContentType.JSON)
            body(jsonBody)
        } When {
            post("api/user/validate")
        } Extract {
            response()
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
    }
})