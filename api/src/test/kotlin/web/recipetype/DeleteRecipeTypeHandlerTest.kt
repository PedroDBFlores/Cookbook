package web.recipetype

import errors.RecipeTypeNotFound
import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.DeleteRecipeType
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class DeleteRecipeTypeHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        deleteRecipeType: DeleteRecipeType,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().delete("/api/recipetype/:id", DeleteRecipeTypeHandler(deleteRecipeType))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Delete recipe type") {
        it("deletes a recipe type returning 204") {
            val deleteRecipeTypeMock = mockk<DeleteRecipeType> {
                every { this@mockk(any()) } just runs
            }

            val requestBuilder = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI("http://localhost:9000/api/recipetype/1"))

            val response = executeRequest(deleteRecipeTypeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.NO_CONTENT_204)
                verify(exactly = 1) { deleteRecipeTypeMock(DeleteRecipeType.Parameters(1)) }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val deleteRecipeTypeMock = mockk<DeleteRecipeType> {
                every { this@mockk(any()) } throws RecipeTypeNotFound(9999)
            }
            val requestBuilder = HttpRequest.newBuilder()
                .DELETE().uri(URI("http://localhost:9000/api/recipetype/9999"))

            val response = executeRequest(deleteRecipeTypeMock, requestBuilder)

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        it("should return 400 if a wrong recipeTypeId is sent") {
            val deleteRecipeTypeMock = mockk<DeleteRecipeType>()
            val requestBuilder = HttpRequest.newBuilder()
                .DELETE().uri(URI("http://localhost:9000/api/recipetype/arroz"))

            val response = executeRequest(deleteRecipeTypeMock, requestBuilder)

            with(response){
                statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                verify(exactly = 0) { deleteRecipeTypeMock(any()) }
            }
        }
    }
})