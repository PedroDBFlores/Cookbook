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
import usecases.role.GetAllRoles
import utils.JsonHelpers.toJson

internal class GetAllRolesHandlerTest : DescribeSpec({
    fun createTestServer(getAllRoles: GetAllRoles): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/role") { GetAllRolesHandler(getAllRoles).handle(call) }
        }
    }

    describe("Get all roles handler") {
        it("gets all the roles") {
            val expectedRoles = listOf(
                Role(id = 1, name = "User", code = "USER"),
                Role(id = 1, name = "Admin", code = "ADMIN")
            )
            val getAllRoles = mockk<GetAllRoles> {
                every { this@mockk() } returns expectedRoles
            }

            withTestApplication(moduleFunction = createTestServer(getAllRoles)) {
                with(handleRequest(HttpMethod.Get, "/role")) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedRoles.toJson())
                    verify(exactly = 1) { getAllRoles() }
                }
            }
        }
    }
})
