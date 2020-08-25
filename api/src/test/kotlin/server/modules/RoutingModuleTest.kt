package server.modules

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
            //Recipe type
            bind<FindRecipeType>() with singleton { mockk(relaxed = true) }
            bind<GetAllRecipeTypes>() with singleton { mockk(relaxed = true) }
            bind<CreateRecipeType>() with singleton { mockk(relaxed = true) }
            bind<UpdateRecipeType>() with singleton { mockk(relaxed = true) }
            bind<DeleteRecipeType>() with singleton { mockk(relaxed = true) }
            //Recipe
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
                with(handleRequest(HttpMethod.Options, "/recipetype")) {
                    with(response) {
                        headers["Allow"].shouldBe("GET,POST,PUT")
                    }
                }
                with(handleRequest(HttpMethod.Options, "/recipetype/123")) {
                    with(response) {
                        headers["Allow"].shouldBe("GET,DELETE")
                    }
                }
            }
        }

        it("checks that the routes for the recipe are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/recipe")) {
                    with(response) {
                        headers["Allow"].shouldBe("GET,POST,PUT")
                    }
                }
                with(handleRequest(HttpMethod.Options, "/recipe/123")) {
                    with(response) {
                        headers["Allow"].shouldBe("GET,DELETE")
                    }
                }
                with(handleRequest(HttpMethod.Options, "/recipe/search")) {
                    with(response) {
                        headers["Allow"].shouldBe("POST")
                    }
                }
            }
        }

    }
})