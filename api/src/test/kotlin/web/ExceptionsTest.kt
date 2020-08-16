package web

import config.Modules
import config.RecipeTypeDependencies
import config.Router
import errors.ValidationError
import io.javalin.http.BadRequestResponse
import io.kotest.assertions.fail
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.jetty.http.HttpStatus

internal class ExceptionsTest : DescribeSpec({

    fun executeRequest(
        recipeTypeDependencies: RecipeTypeDependencies,
        call: () -> Response
    ): Response {
        var app: CookbookApi? = null
        try {
            app = CookbookApi(
                config = Modules.cookbookApiDependencies.configurationFile,
                javalinPlugins = listOf(),
                router = Router(recipeTypeDependencies, mockk(relaxed = true))
            )
            require(app != null) { fail("Javalin failed to initialize") }
            app!!.start()

            return call()
        } finally {
            app?.close()
        }
    }

    describe("Exception test") {
        it("returns 500 on an unexpected exception") {
            val recipeTypeDependencies = mockk<RecipeTypeDependencies>(relaxed = true) {
                every { getAllRecipeTypes() } throws Exception("OOPS")
            }

            val response = executeRequest(recipeTypeDependencies) {
                When {
                    get("http://localhost:9000/api/recipetype")
                } Extract {
                    response()
                }
            }

            with(response) {
                statusCode.shouldBe(HttpStatus.INTERNAL_SERVER_ERROR_500)
                with(body.asString()) {
                    shouldContainJsonKeyValue("code", "INTERNAL_SERVER_ERROR")
                    shouldContainJsonKey("message")
                }
            }
        }

        it("returns a structured error on a BadRequestResponse") {
            val recipeTypeDependencies = mockk<RecipeTypeDependencies>(relaxed = true) {
                every { createRecipeType(any()) } throws BadRequestResponse()
            }

            val response = executeRequest(recipeTypeDependencies) {
                Given {
                    body("""{ "name" : "" }""")
                }
                When {
                    post("http://localhost:9000/api/recipetype")
                } Extract {
                    response()
                }
            }

            with(response) {
                statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                with(body.asString()) {
                    shouldContainJsonKeyValue("code", "BAD_REQUEST")
                    shouldContainJsonKey("message")
                }
            }
        }

        it("returns a structured error on a ValidationError") {
            val recipeTypeDependencies = mockk<RecipeTypeDependencies>(relaxed = true) {
                every { createRecipeType(any()) } throws ValidationError("name")
            }

            val response = executeRequest(recipeTypeDependencies) {
                Given {
                    body("""{ "name" : "" }""")
                }
                When {
                    post("http://localhost:9000/api/recipetype")
                } Extract {
                    response()
                }
            }

            with(response) {
                statusCode.shouldBe(HttpStatus.BAD_REQUEST_400)
                with(body.asString()) {
                    shouldContainJsonKeyValue("code", "BAD_REQUEST")
                    shouldContainJsonKey("message")
                }
            }
        }
    }
})
