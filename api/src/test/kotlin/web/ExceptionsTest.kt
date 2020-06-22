package web

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jetty.http.HttpStatus
import ports.RecipeTypeDependencies
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
        it("returns 500 on an unexpected exception") {
            every { recipeTypeDependencies.getAllRecipeTypes() } throws Exception("OOPS")
            val request = HttpRequest.newBuilder()
                .GET().uri(URI("http://localhost:9000/api/recipetype"))

            val response = HttpClient.newHttpClient()
                .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                .join()

            with(response) {
                statusCode().shouldBe(HttpStatus.INTERNAL_SERVER_ERROR_500)
                with(body()) {
                    shouldContainJsonKeyValue("code", "INTERNAL_SERVER_ERROR")
                    shouldContainJsonKey("message")
                }
            }
        }

        it("returns a structured error on a BadRequestResponse") {
            val request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("""{ "name" : "" }"""))
                .uri(URI("http://localhost:9000/api/recipetype"))

            val response = HttpClient.newHttpClient()
                .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                .join()

            with(response) {
                statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                with(body()) {
                    shouldContainJsonKeyValue("code", "BAD_REQUEST")
                    shouldContainJsonKey("message")
                }
            }
        }
    }
})