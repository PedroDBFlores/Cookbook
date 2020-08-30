package web.roles

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
import model.Role
import server.modules.contentNegotiationModule
import usecases.role.FindRole
import utils.JsonHelpers.toJson

internal class FindRoleHandlerTest : DescribeSpec({
    fun createTestServer(findRole: FindRole): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/role/{code}") { FindRoleHandler(findRole).handle(call) }
        }
    }

    describe("Find role handler") {
        it("returns a role with status code 200") {
            val expectedRole = Role(id = 1, name = "Role", code = "ROLE")
            val findRole = mockk<FindRole> {
                every { this@mockk(FindRole.Parameters(expectedRole.code)) } returns expectedRole
            }

            withTestApplication(moduleFunction = createTestServer(findRole)) {
                with(handleRequest(HttpMethod.Get, "/role/${expectedRole.code}")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedRole.toJson())
                    verify(exactly = 1) { findRole(FindRole.Parameters(expectedRole.code)) }
                }
            }
        }
    }
})