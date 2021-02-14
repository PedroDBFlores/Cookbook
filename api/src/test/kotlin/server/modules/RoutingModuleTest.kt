package server.modules

import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*

class RoutingModuleTest : DescribeSpec({

    fun createTestServer(): Application.() -> Unit = {
        contentNegotiationModule()
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
