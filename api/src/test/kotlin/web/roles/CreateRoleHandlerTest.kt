package web.roles

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.CreateResult
import model.Role
import server.modules.contentNegotiationModule
import usecases.role.CreateRole
import utils.JsonHelpers
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class CreateRoleHandlerTest : DescribeSpec({

    fun createTestServer(createRole: CreateRole): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/role") { CreateRoleHandler(createRole).handle(call) }
        }
    }

    describe("Create role handler") {
        it("creates a role returning 201") {
            val expectedRole = Role(name = "Name", code = "Code")
            val jsonBody = createJSONObject(
                "name" to expectedRole.name,
                "code" to expectedRole.code
            )
            val createRole = mockk<CreateRole> {
                every { this@mockk(CreateRole.Parameters(expectedRole.name, expectedRole.code)) } returns 1
            }

            withTestApplication(moduleFunction = createTestServer(createRole)) {
                with(handleRequest(HttpMethod.Post, "/role") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson(CreateResult(1).toJson())
                    verify(exactly = 1) { createRole(CreateRole.Parameters(expectedRole.name, expectedRole.code)) }
                }
            }
        }

        val createRoleRequestMap = mapOf<String, String>(
            "name" to "name",
            "code" to "code"
        )

        arrayOf(
            row(createJSONObject("non" to "conformant"), "the provided body doesn't match the required JSON"),
            row((createRoleRequestMap + mapOf("name" to "")).toJson(), "the name is empty"),
            row((createRoleRequestMap + mapOf("code" to "")).toJson(), "the code is empty")
        ).forEach { (requestBody, description) ->
            it("returns 400 when $description") {
                val createRole = mockk<CreateRole>()

                withTestApplication(moduleFunction = createTestServer(createRole)) {
                    with(handleRequest(HttpMethod.Post, "/role") {
                        setBody(requestBody)
                        addHeader("Content-Type", "application/json")

                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { createRole wasNot Called }
                    }
                }
            }
        }
    }
})