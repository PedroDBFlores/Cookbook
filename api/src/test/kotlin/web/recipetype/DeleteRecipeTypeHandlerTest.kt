package web.recipetype

import errors.RecipeTypeNotFound
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import usecases.recipetype.DeleteRecipeType

internal class DeleteRecipeTypeHandlerTest : DescribeSpec({
    fun createTestServer(deleteRecipeType: DeleteRecipeType): Application.() -> Unit = {
        routing {
            delete("/api/recipetype/{id}") { DeleteRecipeTypeHandler(deleteRecipeType).handle(call) }
        }
    }

    describe("Delete recipe type handler") {
        it("deletes a recipe type returning 204") {
            val deleteRecipeTypeMock = mockk<DeleteRecipeType> {
                every { this@mockk(any()) } just runs
            }

            withTestApplication(moduleFunction = createTestServer(deleteRecipeTypeMock)) {
                with(handleRequest(HttpMethod.Delete, "/api/recipetype/1")) {
                    response.status().shouldBe(HttpStatusCode.NoContent)
                    verify(exactly = 1) { deleteRecipeTypeMock(DeleteRecipeType.Parameters(1)) }
                }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val deleteRecipeTypeMock = mockk<DeleteRecipeType> {
                every { this@mockk(any()) } throws RecipeTypeNotFound(9999)
            }

            withTestApplication(moduleFunction = createTestServer(deleteRecipeTypeMock)) {
                with(handleRequest(HttpMethod.Delete, "/api/recipetype/9999")) {
                    response.status().shouldBe(HttpStatusCode.NotFound)
                    verify(exactly = 1) { deleteRecipeTypeMock(DeleteRecipeType.Parameters(9999)) }
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
                val deleteRecipeTypeMock = mockk<DeleteRecipeType>()

                withTestApplication(moduleFunction = createTestServer(deleteRecipeTypeMock)) {
                    with(handleRequest(HttpMethod.Delete, "/api/recipetype/$pathParam")) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { deleteRecipeTypeMock wasNot called }
                    }
                }
            }
        }
    }
})
