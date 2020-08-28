package web.user

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.user.DeleteUser

internal class DeleteUserHandlerTest : DescribeSpec({
    fun createTestServer(deleteUser: DeleteUser): Application.() -> Unit = {
        routing {
            delete("/user/{id}") { DeleteUserHandler(deleteUser).handle(call) }
        }
    }

    describe("Delete user handler") {
        it("deletes a user") {
            val deleteUser = mockk<DeleteUser>{
                every { this@mockk(DeleteUser.Parameters(88)) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteUser)) {
                with(handleRequest(HttpMethod.Delete, "/user/88")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteUser(DeleteUser.Parameters(88)) }
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
                val deleteUser = mockk<DeleteUser>()

                withTestApplication(moduleFunction = createTestServer(deleteUser)) {
                    with(handleRequest(HttpMethod.Delete, "/user/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteUser wasNot called }
                    }
                }
            }
        }
    }
})