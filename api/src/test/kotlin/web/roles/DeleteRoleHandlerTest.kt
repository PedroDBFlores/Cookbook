package web.roles

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.role.DeleteRole

internal class DeleteRoleHandlerTest : DescribeSpec({
    fun createTestServer(deleteRole: DeleteRole): Application.() -> Unit = {
        routing {
            delete("/role/{id}") { DeleteRoleHandler(deleteRole).handle(call) }
        }
    }

    describe("Delete role handler") {
        it("deletes a role returning 204") {
            val deleteRole = mockk<DeleteRole> {
                every { this@mockk(any()) } just Runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteRole)) {
                with(handleRequest(HttpMethod.Delete, "/role/1")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteRole(DeleteRole.Parameters(1)) }
                }
            }
        }

        arrayOf(
            row(
                "massa",
                "a non-number is provided"
            ),
            row(
                "-99",
                "an invalid id is provided",
            )
        ).forEach { (pathParam, description) ->
            it("should return 400 if $description") {
                val deleteRole = mockk<DeleteRole>()

                withTestApplication(moduleFunction = createTestServer(deleteRole)) {
                    with(handleRequest(HttpMethod.Delete, "/role/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteRole wasNot called }
                    }
                }
            }
        }
    }
})