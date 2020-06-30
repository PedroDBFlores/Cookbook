package web.recipetype

import errors.RecipeTypeNotFound
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
import usecases.recipetype.FindRecipeType
import utils.DTOGenerator.generateRecipeType
import utils.convertToJSON

class FindRecipeTypeHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        findRecipeType: FindRecipeType,
        recipeTypeIdParam: String
    ): Response {
        app = Javalin.create().get("/api/recipetype/:id", FindRecipeTypeHandler(findRecipeType))
            .start(9000)

        return Given {
            pathParam("id", recipeTypeIdParam)
        } When {
            get("/api/recipetype/{id}")
        } Extract {
            response()
        }
    }

    describe("Find recipe type handler") {
        it("returns a recipe type with status code 200") {
            val expectedRecipeType = generateRecipeType()
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(expectedRecipeType.id)) } returns expectedRecipeType
            }

            val response = executeRequest(getRecipeTypeMock, expectedRecipeType.id.toString())

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                //body.`as`<RecipeType>(RecipeType::class.java).shouldBe(expectedRecipeType)
                body.asString().shouldMatchJson(convertToJSON(expectedRecipeType))
                verify { getRecipeTypeMock(FindRecipeType.Parameters(expectedRecipeType.id)) }
            }
        }

        it("should return a 404 if the recipe type wasn't found") {
            val getRecipeTypeMock = mockk<FindRecipeType> {
                every { this@mockk(FindRecipeType.Parameters(9999)) } throws RecipeTypeNotFound(9999)
            }

            val response = executeRequest(getRecipeTypeMock, "9999")

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
            it("should return BAD_REQUEST if $description") {
                val getRecipeTypeMock = mockk<FindRecipeType>()

                val response = executeRequest(getRecipeTypeMock, pathParam)

                with(response) {
                    statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                    body.asString().shouldContain(messageToContain)
                }
                verify(exactly = 0) { getRecipeTypeMock(any()) }
            }
        }
    }
})