package web

import config.KoinModules
import config.RecipeDependencies
import config.RecipeTypeDependencies
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
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

internal class ExceptionsTest : KoinComponent, DescribeSpec({

    fun executeRequest(
        recipeTypeDependencies: RecipeTypeDependencies,
        call: () -> Response
    ): Response {
        var app: CookbookApi? = null
        try {
            val koin = startKoin {
                loadKoinModules(listOf(KoinModules.baseModule, module(override = true) {
                    single { recipeTypeDependencies }
                    single { mockk<RecipeDependencies>(relaxed = true) }
                }))
            }

            app = CookbookApi(
                config = koin.koin.get(),
                javalinPlugins = koin.koin.get(),
                router = koin.koin.get(),
                onStop = { stopKoin() }
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
