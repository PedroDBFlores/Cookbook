package web.userroles

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.userroles.DeleteRoleFromUser

internal class DeleteRoleFromUserHandlerTest : DescribeSpec({
    fun createTestServer(deleteRoleFromUser: DeleteRoleFromUser): Application.() -> Unit = {
        routing {
            delete("/userroles/{userId}/{roleId}") { DeleteRoleFromUserHandler(deleteRoleFromUser).handle(call) }
        }
    }

    describe("Delete role from user handler") {
        it("deletes a role from an user") {
            val deleteRoleFromUser = mockk<DeleteRoleFromUser> {
                every { this@mockk(DeleteRoleFromUser.Parameters(34, 12)) } just Runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteRoleFromUser)) {
                with(handleRequest(HttpMethod.Delete, "/userroles/34/12")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteRoleFromUser(DeleteRoleFromUser.Parameters(34, 12)) }
                }
            }
        }

        arrayOf(
            row(
                "massa",
                "arroz",
                "a non-number is provided"
            ),
            row(
                "-99",
                "1",
                "an invalid userId is provided",
            ),
            row(
                "1",
                "-99",
                "an invalid roleId is provided",
            )
        ).forEach { (userIdPathParam, roleIdPathParam, description) ->
            it("should return 400 if $description") {
                val deleteRoleFromUser = mockk<DeleteRoleFromUser>()

                withTestApplication(moduleFunction = createTestServer(deleteRoleFromUser)) {
                    with(handleRequest(HttpMethod.Delete, "/userroles/$userIdPathParam/$roleIdPathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteRoleFromUser wasNot called }
                    }
                }
            }
        }
    }
})