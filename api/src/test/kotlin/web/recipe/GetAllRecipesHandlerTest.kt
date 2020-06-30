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

class GetAllRecipesHandlerTest : DescribeSpec({
    var app: Javalin? = null

    beforeSpec {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 9000
    }

    afterTest {
        app?.stop()
    }

    fun executeRequest(
        getAllRecipes: GetAllRecipes
    ): Response {
        app = Javalin.create().get("/api/recipe", GetAllRecipesHandler(getAllRecipes))
            .start(9000)

        return When {
            get("/api/recipe")
        } Extract {
            response()
        }
    }

    describe("Get all recipes handler") {
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
})