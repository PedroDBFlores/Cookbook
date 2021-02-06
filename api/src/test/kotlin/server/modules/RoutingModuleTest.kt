package server.modules

import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import org.kodein.di.bind
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import usecases.recipe.*
import usecases.recipetype.*

class RoutingModuleTest : DescribeSpec({

    fun Application.injectTestDependencies() {
        di {
            // Recipe type
            bind<FindRecipeType>() with singleton { mockk(relaxed = true) }
            bind<GetAllRecipeTypes>() with singleton { mockk(relaxed = true) }
            bind<CreateRecipeType>() with singleton { mockk(relaxed = true) }
            bind<UpdateRecipeType>() with singleton { mockk(relaxed = true) }
            bind<DeleteRecipeType>() with singleton { mockk(relaxed = true) }
            // Recipe
            bind<FindRecipe>() with singleton { mockk(relaxed = true) }
            bind<GetAllRecipes>() with singleton { mockk(relaxed = true) }
            bind<CreateRecipe>() with singleton { mockk(relaxed = true) }
            bind<UpdateRecipe>() with singleton { mockk(relaxed = true) }
            bind<DeleteRecipe>() with singleton { mockk(relaxed = true) }
        }
    }

    fun createTestServer(): Application.() -> Unit = {
        contentNegotiationModule()
        injectTestDependencies()
        routingModule()
    }

    describe("Routing module test") {
        it("checks that the health check endpoint is mapped") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Get, "/health-check")) {
                    with(response) {
                        status().shouldBe(HttpStatusCode.OK)
                        content.shouldBe("I'm alive, thanks for asking 👋")
                    }
                }
            }
        }

        it("checks that the routes for the recipe types are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/api/recipetype")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,POST,PUT")
                }
                with(handleRequest(HttpMethod.Options, "/api/recipetype/123")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
                }
            }
        }

        it("checks that the routes for the recipe are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/api/recipe")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,POST,PUT")
                }
                with(handleRequest(HttpMethod.Options, "/api/recipe/123")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
                }
                with(handleRequest(HttpMethod.Options, "/api/recipe/search")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "POST")
                }
            }
        }
    }
})
