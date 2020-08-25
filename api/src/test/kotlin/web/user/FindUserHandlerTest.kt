package web.user

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
import model.User
import server.modules.contentNegotiationModule
import usecases.user.FindUser
import utils.JsonHelpers.toJson

internal class FindUserHandlerTest : DescribeSpec({

    fun createTestServer(findUser: FindUser): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/user/{id}") { FindUserHandler(findUser).handle(call) }
        }
    }

    describe("Find user handler") {
        it("returns 200 with the requested user") {
            val expectedUser = User(id = 123, name = "name", userName = "username")
            val findUser = mockk<FindUser> {
                every { this@mockk(FindUser.Parameters(expectedUser.id)) } returns expectedUser
            }
            withTestApplication(moduleFunction = createTestServer(findUser)) {
                with(handleRequest(HttpMethod.Get, "/user/${expectedUser.id}") {
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedUser.toJson())
                    verify(exactly = 1) { findUser(FindUser.Parameters(expectedUser.id)) }
                }
            }
        }

        it("should return 404 if the user is not found") {
            val findUser = mockk<FindUser> {
                every { this@mockk(FindUser.Parameters(9999)) } throws UserNotFound()
            }
            withTestApplication(moduleFunction = createTestServer(findUser)) {
                with(handleRequest(HttpMethod.Get, "/user/9999") {
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.NotFound)
                    verify(exactly = 1) { findUser(FindUser.Parameters(9999)) }
                }
            }
        }

        arrayOf(
            row(
                "arroz",
                "a non-number is provided"
            ),
            row(
                "-99",
                "an invalid id is provided"
            )
        ).forEach { (pathParam, description) ->
            it("should return BAD_REQUEST if $description") {
                val findUser = mockk<FindUser>()

                withTestApplication(moduleFunction = createTestServer(findUser)) {
                    with(handleRequest(HttpMethod.Get, "/user/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { findUser wasNot called }
                    }
                }
            }
        }
    }
})