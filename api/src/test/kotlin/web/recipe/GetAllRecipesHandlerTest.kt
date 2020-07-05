package web.recipe

import io.javalin.Javalin
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus
import usecases.recipe.GetAllRecipes
import utils.DTOGenerator
import utils.convertToJSON

internal class GetAllRecipesHandlerTest : DescribeSpec({

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    fun executeRequest(
        getAllRecipes: GetAllRecipes
    ): Response {
        val app = Javalin.create().get("/api/recipe", GetAllRecipesHandler(getAllRecipes))
            .start(9000)

        try {
            return When {
                get("/api/recipe")
            } Extract {
                response()
            }
        } finally {
            app.stop()
        }
    }

    describe("Get all recipes handler") {
        it("gets all the recipes") {
            val expectedRecipes = listOf(
                DTOGenerator.generateRecipe(),
                DTOGenerator.generateRecipe()
            )
            val getAllRecipesMock = mockk<GetAllRecipes> {
                every { this@mockk() } returns expectedRecipes
            }

            val response = executeRequest(getAllRecipesMock)

            with(response) {
                statusCode.shouldBe(HttpStatus.OK_200)
                body.asString().shouldMatchJson(convertToJSON(expectedRecipes))
                verify(exactly = 1) { getAllRecipesMock() }
            }
        }
    }
})
