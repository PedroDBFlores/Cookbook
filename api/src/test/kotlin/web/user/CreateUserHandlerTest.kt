package web.user

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
import model.CreateResult
import model.User
import server.modules.contentNegotiationModule
import usecases.user.CreateUser
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class CreateUserHandlerTest : DescribeSpec({

    fun createTestServer(createUser: CreateUser): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/user") { CreateUserHandler(createUser).handle(call) }
        }
    }

    describe("Create user handler") {
        it("returns 201 on a successful creation") {
            val expectedUser = User(id = 123, name = "name", userName = "username")
            val jsonBody = createJSONObject(
                "name" to expectedUser.name,
                "userName" to expectedUser.userName,
                "password" to "password"
            )

            val createUser = mockk<CreateUser> {
                every {
                    this@mockk(
                        CreateUser.Parameters(
                            expectedUser.copy(id = 0, passwordHash = ""),
                            "password"
                        )
                    )
                } returns expectedUser.id
            }

            withTestApplication(moduleFunction = createTestServer(createUser)) {
                with(handleRequest(HttpMethod.Post, "/user") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson(CreateResult(expectedUser.id).toJson())
                    verify(exactly = 1) {
                        createUser(CreateUser.Parameters(expectedUser.copy(id = 0, passwordHash = ""), "password"))
                    }
                }
            }
        }

        val createUserRepresenterMap = mapOf<String, Any>(
            "name" to "name",
            "userName" to "username",
            "password" to "password"
        )

        arrayOf(
            row(createJSONObject("non" to "conformant"), "the provided body doesn't match the required JSON"),
            row((createUserRepresenterMap + mapOf<String, Any>("name" to "")).toJson(), "the name field is invalid"),
            row(
                (createUserRepresenterMap + mapOf<String, Any>("userName" to "")).toJson(),
                "the userName field is invalid"
            ),
            row(
                (createUserRepresenterMap + mapOf<String, Any>("password" to "")).toJson(),
                "the password field is invalid"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 when $description") {
                val createUser = mockk<CreateUser>()

                withTestApplication(moduleFunction = createTestServer(createUser)) {
                    with(handleRequest(HttpMethod.Post, "/user") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { createUser wasNot called }
                    }
                }
            }
        }
    }
})