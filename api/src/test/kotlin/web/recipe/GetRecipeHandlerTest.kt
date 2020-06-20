package web.recipe

import errors.RecipeNotFound
import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.GetRecipe
import utils.DTOGenerator
import utils.convertToJSON
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class GetRecipeHandlerTest : DescribeSpec({
    lateinit var app: Javalin

    afterTest {
        app.stop()
    }

    fun executeRequest(
        getRecipe: GetRecipe,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().get("/api/recipe/:id", GetRecipeHandler(getRecipe))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Get recipe handler") {
        it("returns a recipe with status code 200") {
            val expectedRecipe = DTOGenerator.generateRecipe()
            val getRecipeMock = mockk<GetRecipe> {
                every { this@mockk(GetRecipe.Parameters(expectedRecipe.id)) } returns expectedRecipe
            }

            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipe/${expectedRecipe.id}"))

            val response = executeRequest(getRecipeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.OK_200)
                body().shouldMatchJson(convertToJSON(expectedRecipe))
                verify { getRecipeMock(GetRecipe.Parameters(expectedRecipe.id)) }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val getRecipeMock = mockk<GetRecipe> {
                every { this@mockk(GetRecipe.Parameters(9999)) } throws RecipeNotFound(9999)
            }
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipe/9999"))

            val response = executeRequest(getRecipeMock, requestBuilder)

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        it("should return 400 if a wrong recipeTypeId is sent") {
            val getRecipeMock = mockk<GetRecipe>()
            val requestBuilder = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipe/arroz"))

            val response = executeRequest(getRecipeMock, requestBuilder)

            with(response) {
                statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                verify(exactly = 0) { getRecipeMock(any()) }
            }
        }
    }
})