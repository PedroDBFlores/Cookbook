package web.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.recipetype.DeleteRecipeType

internal class DeleteRecipeTypeHandlerTest : DescribeSpec({
    fun createTestServer(deleteRecipeType: DeleteRecipeType): Application.() -> Unit = {
        routing {
            delete("/recipetype/{id}") { DeleteRecipeTypeHandler(deleteRecipeType).handle(call) }
        }
    }

    describe("Delete recipe type handler") {
        it("deletes a recipe type returning 204") {
            val deleteRecipeType = mockk<DeleteRecipeType> {
                every { this@mockk(any()) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteRecipeType)) {
                with(handleRequest(HttpMethod.Delete, "/recipetype/1")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteRecipeType(DeleteRecipeType.Parameters(1)) }
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
                val deleteRecipeType = mockk<DeleteRecipeType>()

                withTestApplication(moduleFunction = createTestServer(deleteRecipeType)) {
                    with(handleRequest(HttpMethod.Delete, "/recipetype/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteRecipeType wasNot called }
                    }
                }
            }
        }
    }
})
