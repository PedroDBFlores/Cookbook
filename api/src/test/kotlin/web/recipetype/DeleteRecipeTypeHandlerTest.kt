package web.recipetype

import errors.RecipeTypeNotFound
import io.javalin.Javalin
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.*
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipetype.DeleteRecipeType
import java.net.URI
import java.net.http.HttpRequest

class DeleteRecipeTypeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        deleteRecipeType: DeleteRecipeType,
        recipeTypeIdParam: String
    ): Response {
        app = Javalin.create().delete("/api/recipetype/:id", DeleteRecipeTypeHandler(deleteRecipeType))
            .start(9000)

        return Given {
            pathParam("id", recipeTypeIdParam)
        } When {
            delete("/api/recipetype/{id}")
        } Extract {
            response()
        }
    }

    describe("Delete recipe type handler") {
        it("deletes a recipe type returning 204") {
            val deleteRecipeTypeMock = mockk<DeleteRecipeType> {
                every { this@mockk(any()) } just runs
            }

            val response = executeRequest(deleteRecipeTypeMock, "1")

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

            val response = executeRequest(deleteRecipeTypeMock, "9999")

            response.statusCode().shouldBe(HttpStatus.NOT_FOUND_404)
        }

        arrayOf(
            row(
                "massa",
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
                val deleteRecipeTypeMock = mockk<DeleteRecipeType>()

                val response = executeRequest(deleteRecipeTypeMock, pathParam)

                with(response) {
                    statusCode().shouldBe(HttpStatus.BAD_REQUEST_400)
                    body().asString().shouldContain(messageToContain)
                }
                verify(exactly = 0) { deleteRecipeTypeMock(any()) }
            }
        }
    }
})