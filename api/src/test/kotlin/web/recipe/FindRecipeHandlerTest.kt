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
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.FindRecipe
import utils.DTOGenerator
import utils.convertToJSON

class FindRecipeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        findRecipe: FindRecipe,
        recipeIdParam: String
    ): Response {
        app = Javalin.create().get("/api/recipe/:id", FindRecipeHandler(findRecipe))
            .start(9000)

        return Given {
            pathParam("id", recipeIdParam)
        } When {
            get("/api/recipe/{id}")
        } Extract {
            response()
        }
    }

    describe("Find recipe handler") {
        it("returns a recipe with status code 200") {
            val expectedRecipe = DTOGenerator.generateRecipe()
            val getRecipeMock = mockk<FindRecipe> {
                every { this@mockk(FindRecipe.Parameters(expectedRecipe.id)) } returns expectedRecipe
            }

            val response = executeRequest(getRecipeMock, expectedRecipe.id.toString())

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                body.asString().shouldMatchJson(convertToJSON(expectedRecipe))
                verify { getRecipeMock(FindRecipe.Parameters(expectedRecipe.id)) }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val getRecipeMock = mockk<FindRecipe> {
                every { this@mockk(FindRecipe.Parameters(9999)) } throws RecipeNotFound(9999)
            }

            val response = executeRequest(getRecipeMock, "9999")

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

                val response = executeRequest(getRecipeMock, pathParam)

                with(response) {
                    statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                    body.asString().shouldContain(messageToContain)
                }
                verify(exactly = 0) { getRecipeMock(any()) }
            }
        }
    }
})