package web.user

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.CreateResult
import server.modules.contentNegotiationModule
import usecases.user.CreateUser
import utils.DTOGenerator
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.removePropertiesFromJson
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
            val expectedUser = DTOGenerator.generateUser()
            val jsonBody = createJSONObject(
                mapOf(
                    "name" to expectedUser.name,
                    "userName" to expectedUser.userName,
                    "password" to "password"
                )
            )

            val createUser = mockk<CreateUser> {
                every { this@mockk(expectedUser.copy(id = 0, passwordHash = ""), "password") } returns expectedUser.id
            }

            withTestApplication(moduleFunction = createTestServer(createUser)) {
                with(handleRequest(HttpMethod.Post, "/user") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson(CreateResult(expectedUser.id).toJson())
                    verify(exactly = 1) {
                        createUser(expectedUser.copy(id = 0, passwordHash = ""), "password")
                    }
                }
            }
        }
    }
})