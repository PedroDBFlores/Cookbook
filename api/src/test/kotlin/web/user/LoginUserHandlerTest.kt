package web.user

import errors.WrongCredentials
import errors.UserNotFound
import io.kotest.assertions.json.shouldMatchJson
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
import usecases.user.LoginUser
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class LoginUserHandlerTest : DescribeSpec({
    val credentials = Credentials(userName = "username", password = "password")
    val jsonBody = credentials.toJson()

    fun createTestServer(loginUser: LoginUser): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/user/login") { LoginUserHandler(loginUser).handle(call) }
        }
    }

    describe("Validate user credentials handler") {
        it("returns 200 with a JWT if the user is authorized") {
            val loginUser = mockk<LoginUser> {
                every {
                    this@mockk(
                        LoginUser.Parameters(
                            Credentials(
                                userName = credentials.userName,
                                password = credentials.password
                            )
                        )
                    )
                } returns "A_VALID_TOKEN"
            }

            withTestApplication(moduleFunction = createTestServer(loginUser)) {
                with(handleRequest(HttpMethod.Post, "/user/login") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(
                        createJSONObject("token" to "A_VALID_TOKEN")
                    )
                    verify(exactly = 1) {
                        loginUser.invoke(
                            LoginUser.Parameters(
                                Credentials(
                                    userName = credentials.userName,
                                    password = credentials.password
                                )
                            )
                        )
                    }
                }
            }
        }

        it("returns 403 if the user is not found") {
            val loginUser = mockk<LoginUser> {
                every { this@mockk(any()) } throws UserNotFound()
            }

            withTestApplication(moduleFunction = createTestServer(loginUser)) {
                with(handleRequest(HttpMethod.Post, "/user/login") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.Forbidden)
                    verify(exactly = 1) {
                        loginUser.invoke(
                            LoginUser.Parameters(
                                Credentials(
                                    userName = credentials.userName,
                                    password = credentials.password
                                )
                            )
                        )
                    }
                }
            }
        }

        it("returns 401 if the user credentials aren't valid") {
            val loginUser = mockk<LoginUser> {
                every { this@mockk(any()) } throws WrongCredentials()
            }

            withTestApplication(moduleFunction = createTestServer(loginUser)) {
                with(handleRequest(HttpMethod.Post, "/user/login") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.Unauthorized)
                    verify(exactly = 1) {
                        loginUser.invoke(
                            LoginUser.Parameters(
                                Credentials(
                                    userName = credentials.userName,
                                    password = credentials.password
                                )
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
                val loginUser = mockk<LoginUser>(relaxed = true)

                withTestApplication(moduleFunction = createTestServer(loginUser)) {
                    with(handleRequest(HttpMethod.Post, "/user/login") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    })
                    {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { loginUser wasNot called }
                    }
                }
            }
        }
    }
})
