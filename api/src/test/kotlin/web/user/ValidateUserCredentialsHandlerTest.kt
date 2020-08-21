package web.user

import errors.PasswordMismatchError
import errors.UserNotFound
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Credentials
import server.modules.contentNegotiationModule
import usecases.user.ValidateUserCredentials
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class ValidateUserCredentialsHandlerTest : DescribeSpec({
    val credentials = Credentials(username = "username", password = "password")
    val jsonBody = credentials.toJson()

    fun createTestServer(validateUserCredentials: ValidateUserCredentials): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/user/validate") { ValidateUserCredentialsHandler(validateUserCredentials).handle(call) }
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

            withTestApplication(moduleFunction = createTestServer(validateUserCredentials)) {
                with(handleRequest(HttpMethod.Post, "/user/validate") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldBe("A_VALID_TOKEN")
                    verify(exactly = 1) {
                        validateUserCredentials.invoke(
                            Credentials(
                                username = credentials.username,
                                password = credentials.password
                            )
                        )
                    }
                }
            }
        }

        it("returns 403 if the user is not found") {
            val validateUserCredentials = mockk<ValidateUserCredentials> {
                every { this@mockk(any()) } throws UserNotFound()
            }

            withTestApplication(moduleFunction = createTestServer(validateUserCredentials)) {
                with(handleRequest(HttpMethod.Post, "/user/validate") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.Forbidden)
                    verify(exactly = 1) {
                        validateUserCredentials.invoke(
                            Credentials(
                                username = credentials.username,
                                password = credentials.password
                            )
                        )
                    }
                }
            }
        }

        it("returns 401 if the user credentials aren't valid") {
            val validateUserCredentials = mockk<ValidateUserCredentials> {
                every { this@mockk(any()) } throws PasswordMismatchError()
            }

            withTestApplication(moduleFunction = createTestServer(validateUserCredentials)) {
                with(handleRequest(HttpMethod.Post, "/user/validate") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.Unauthorized)
                    verify(exactly = 1) {
                        validateUserCredentials.invoke(
                            Credentials(
                                username = credentials.username,
                                password = credentials.password
                            )
                        )
                    }
                }
            }
        }

        arrayOf(
            row(
                createJSONObject(),
                "when there is no body"
            ),
            row(
                createJSONObject("non" to "conformant"),
                "an invalid body is provided"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 $description") {
                val validateUserCredentials = mockk<ValidateUserCredentials>(relaxed = true)

                withTestApplication(moduleFunction = createTestServer(validateUserCredentials)) {
                    with(handleRequest(HttpMethod.Post, "/user/validate") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    })
                    {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { validateUserCredentials wasNot called }
                    }
                }
            }
        }
    }
})
