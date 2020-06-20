package web

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import ports.RecipeTypeDependencies
import utils.convertToJSON
import java.lang.Exception
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class ExceptionsTest : DescribeSpec({
    lateinit var app: CookbookApi
    val recipeTypeDependencies = mockk<RecipeTypeDependencies>(relaxed = true)

    beforeTest {
        //clearMocks(recipeTypeDependencies)
    }

    beforeSpec {
        clearMocks(recipeTypeDependencies)
        app = CookbookApi(
            port = 9000,
            recipeTypeDependencies = recipeTypeDependencies,
            recipeDependencies = mockk(relaxed = true),
            plugins = emptyList()
        )
        app.start()
    }

    afterSpec {
        app.close()
    }

    describe("Exception test") {
        it("throws") {
            every { recipeTypeDependencies.getAllRecipeTypes() } throws Exception("OOPS")
            val request = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipetype"))

            val response = HttpClient.newHttpClient()
                .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                .join()

            response.statusCode().shouldBe(HttpStatus.INTERNAL_SERVER_ERROR_500)
            response.body().shouldMatchJson(
                convertToJSON(
                    ResponseError(
                        code = "INTERNAL_SERVER_ERROR",
                        message = "Unexpected error (Exception): OOPS"
                    )
                )
            )

            verify(exactly = 1) { recipeTypeDependencies.getAllRecipeTypes() }
        }
    }
})