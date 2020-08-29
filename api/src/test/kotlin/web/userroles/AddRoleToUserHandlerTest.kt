package web.userroles

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import server.modules.contentNegotiationModule
import usecases.userroles.AddRoleToUser
import utils.JsonHelpers.createJSONObject

internal class AddRoleToUserHandlerTest : DescribeSpec({

    fun createTestServer(addRoleToUser: AddRoleToUser): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/userroles") { AddRoleToUserHandler(addRoleToUser).handle(call) }
        }
    }

    describe("Add role to user handler") {
        it("adds the role to a specific user") {
            val requestBody = createJSONObject(
                "userId" to 1,
                "roleId" to 2
            )
            val addRoleToUser = mockk<AddRoleToUser> {
                every { this@mockk(AddRoleToUser.Parameters(1, 2)) } just Runs
            }

            withTestApplication(moduleFunction = createTestServer(addRoleToUser)) {
                with(handleRequest(HttpMethod.Post, "/userroles") {
                    setBody(requestBody)
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.Created)
                    verify(exactly = 1) {
                        addRoleToUser(AddRoleToUser.Parameters(1, 2))
                    }
                }
            }
        }

        arrayOf(
            row(createJSONObject("non" to "conformant"), "the provided JSON is in the wrong format"),
            row(createJSONObject("userId" to null, "roleId" to 2), "a null userId is provided"),
            row(createJSONObject("userId" to 1, "roleId" to null), "a null roleId is provided")
        ).forEach { (requestBody, description) ->
            it("returns 400 when $description") {
                val addRoleToUser = mockk<AddRoleToUser>()

                withTestApplication(moduleFunction = createTestServer(addRoleToUser)) {
                    with(handleRequest(HttpMethod.Post, "/userroles") {
                        setBody(requestBody)
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { addRoleToUser wasNot called }
                    }
                }
            }
        }
    }
})