package web.recipe

import errors.RecipeNotFound
import errors.RecipeTypeNotFound
import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.DeleteRecipe
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class DeleteRecipeHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        deleteRecipe: DeleteRecipe,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().delete("/api/recipe/:id", DeleteRecipeHandler(deleteRecipe))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Delete recipe handler") {
        it("deletes a recipe returning 204") {
            val deleteRecipeMock = mockk<DeleteRecipe> {
                every { this@mockk(any()) } just runs
            }

            val requestBuilder = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI("http://localhost:9000/api/recipe/1"))

            val response = executeRequest(deleteRecipeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.NO_CONTENT_204)
                verify(exactly = 1) { deleteRecipeMock(DeleteRecipe.Parameters(1)) }
            }
        }

        it("should return a 404 if the recipe wasn't found") {
            val deleteRecipeMock = mockk<DeleteRecipe> {
                every { this@mockk(any()) } throws RecipeNotFound(9999)
            }
            val requestBuilder = HttpRequest.newBuilder()
                .DELETE().uri(URI("http://localhost:9000/api/recipe/9999"))

            val response = executeRequest(deleteRecipeMock, requestBuilder)

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        it("should return 400 if a wrong recipeId was sent") {
            val deleteRecipeMock = mockk<DeleteRecipe>()
            val requestBuilder = HttpRequest.newBuilder()
                .DELETE().uri(URI("http://localhost:9000/api/recipe/massa"))

            val response = executeRequest(deleteRecipeMock, requestBuilder)

            with(response){
                statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                verify(exactly = 0) { deleteRecipeMock(any()) }
            }
        }
    }
})