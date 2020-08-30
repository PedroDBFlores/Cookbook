package web.roles

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import model.Role
import server.modules.contentNegotiationModule
import usecases.role.UpdateRole
import utils.JsonHelpers
import utils.JsonHelpers.toJson

internal class UpdateRoleHandlerTest : DescribeSpec({
    fun createTestServer(updateRole: UpdateRole): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            put("/role") { UpdateRoleHandler(updateRole).handle(call) }
        }
    }

    describe("Update role handler") {
        it("updates a role returning 200") {
            val expectedRole = Role(id = 818, name = "Name", code = "Code")
            val requestBody = JsonHelpers.createJSONObject(
                "id" to expectedRole.id,
                "name" to expectedRole.name,
                "code" to expectedRole.code
            )
            val updateRole = mockk<UpdateRole> {
                every {
                    this@mockk(
                        UpdateRole.Parameters(
                            id = expectedRole.id,
                            name = expectedRole.name,
                            code = expectedRole.code
                        )
                    )
                } just Runs
            }

            withTestApplication(moduleFunction = createTestServer(updateRole)) {
                with(handleRequest(HttpMethod.Put, "/role") {
                    setBody(requestBody)
                    addHeader("Content-Type", "application/json")
                })
                {
                    response.status().shouldBe(HttpStatusCode.OK)
                    verify(exactly = 1) {
                        updateRole(
                            UpdateRole.Parameters(
                                id = expectedRole.id,
                                name = expectedRole.name,
                                code = expectedRole.code
                            )
                        )
                    }
                }
            }
        }

        val updateRoleRequestMap = mapOf(
            "id" to 1,
            "name" to "name",
            "code" to "code"
        )

        arrayOf(
            row(
                JsonHelpers.createJSONObject("non" to "conformant"),
                "the provided body doesn't match the required JSON"
            ),
            row((updateRoleRequestMap + mapOf("id" to 0)).toJson(), "the id is invalid"),
            row((updateRoleRequestMap + mapOf("name" to "")).toJson(), "the name is empty"),
            row((updateRoleRequestMap + mapOf("code" to "")).toJson(), "the code is empty")
        ).forEach { (requestBody, description) ->
            it("returns 400 when $description") {
                val updateRole = mockk<UpdateRole>()

                withTestApplication(moduleFunction = createTestServer(updateRole)) {
                    with(handleRequest(HttpMethod.Put, "/role") {
                        setBody(requestBody)
                        addHeader("Content-Type", "application/json")

                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { updateRole wasNot Called }
                    }
                }
            }
        }
    }
})