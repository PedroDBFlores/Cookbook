package web.userroles

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
import model.UserRole
import server.modules.contentNegotiationModule
import usecases.userroles.GetUserRoles
import utils.JsonHelpers.toJson

internal class GetUserRolesHandlerTest : DescribeSpec({
    fun createTestServer(getUserRoles: GetUserRoles): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/userroles/{id}") { GetUserRolesHandler(getUserRoles).handle(call) }
        }
    }

    describe("Get user roles handler") {
        it("gets the users roles"){
            val userId = 8
            val expectedUserRoles = listOf(
                UserRole(userId, 7),
                UserRole(userId,9)
            )
            val getUserRoles = mockk<GetUserRoles>{
                every { this@mockk(GetUserRoles.Parameters(userId)) } returns expectedUserRoles
            }

            withTestApplication(moduleFunction = createTestServer(getUserRoles)) {
                with(handleRequest(HttpMethod.Get, "/userroles/${userId}") {
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldMatchJson(expectedUserRoles.toJson())
                    verify(exactly = 1) { getUserRoles(GetUserRoles.Parameters(userId)) }
                }
            }
        }
    }
})