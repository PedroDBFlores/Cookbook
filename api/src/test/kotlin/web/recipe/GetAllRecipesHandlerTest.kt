package web.recipe

import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.GetAllRecipes
import utils.DTOGenerator
import utils.convertToJSON
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GetAllRecipesHandlerTest : DescribeSpec({
    var app: Javalin? = null

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        getAllRecipes: GetAllRecipes,
        request: HttpRequest.Builder
    ): HttpResponse<String> {
        app = Javalin.create().get("/api/recipe", GetAllRecipesHandler(getAllRecipes))
            .start(9000)

        return HttpClient.newHttpClient()
            .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
            .join()
    }

    describe("Get all recipes handler") {
        val expectedRecipes = listOf(
            DTOGenerator.generateRecipe(),
            DTOGenerator.generateRecipe()
        )
        val getAllRecipesMock = mockk<GetAllRecipes> {
            every { this@mockk() } returns expectedRecipes
        }

        val requestBuilder = HttpRequest.newBuilder()
            .GET().uri(URI("http://localhost:9000/api/recipe"))

        val response = executeRequest(getAllRecipesMock, requestBuilder)

        with(response) {
            statusCode().shouldBe(HttpStatus.OK_200)
            body().shouldMatchJson(convertToJSON(expectedRecipes))
            verify(exactly = 1) { getAllRecipesMock() }
        }
    }
})