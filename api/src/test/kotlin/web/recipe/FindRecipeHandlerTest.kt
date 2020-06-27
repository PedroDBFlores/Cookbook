package web.recipe

import errors.RecipeNotFound
import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.FindRecipe
import utils.DTOGenerator
import utils.convertToJSON
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class FindRecipeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        findRecipe: FindRecipe,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().get("/api/recipe/:id", FindRecipeHandler(findRecipe))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Find recipe handler") {
        it("returns a recipe with status code 200") {
            val expectedRecipe = DTOGenerator.generateRecipe()
            val getRecipeMock = mockk<FindRecipe> {
                every { this@mockk(FindRecipe.Parameters(expectedRecipe.id)) } returns expectedRecipe
            }

            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipe/${expectedRecipe.id}"))

            val response = executeRequest(getRecipeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.OK_200)
                body().shouldMatchJson(convertToJSON(expectedRecipe))
                verify { getRecipeMock(FindRecipe.Parameters(expectedRecipe.id)) }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val getRecipeMock = mockk<FindRecipe> {
                every { this@mockk(FindRecipe.Parameters(9999)) } throws RecipeNotFound(9999)
            }
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipe/9999"))

            val response = executeRequest(getRecipeMock, requestBuilder)

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        arrayOf(
            row(
                "arroz",
                "a non-number is provided",
                "Path parameter 'id' with value"
            ),
            row(
                "-99",
                "an invalid id is provided",
                "Path param 'id' must be bigger than 0"
            )
        ).forEach { (pathParam, description, messageToContain) ->
            it("should return 400 if $description") {
                val getRecipeMock = mockk<FindRecipe>()
                val requestBuilder = HttpRequest.newBuilder()
                    .GET().uri(URI("http://localhost:9000/api/recipe/$pathParam"))

                val response = executeRequest(getRecipeMock, requestBuilder)

                with(response) {
                    statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                    body().shouldContain(messageToContain)
                }
                verify(exactly = 0) { getRecipeMock(any()) }
            }
        }
    }
})