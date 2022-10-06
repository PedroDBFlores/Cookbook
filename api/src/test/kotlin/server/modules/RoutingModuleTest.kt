package server.modules

import io.kotest.assertions.ktor.client.shouldHaveHeader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*

class RoutingModuleTest : DescribeSpec({

    fun Application.setupTestServer() {
        contentNegotiationModule()
        routingModule()
    }

    it("checks that the health check endpoint is mapped") {
        testApplication {
            application { setupTestServer() }
            val client = createClient { }

            with(client.get("/health-check")) {
                status.shouldBe(HttpStatusCode.OK)
                bodyAsText().shouldBe("I'm alive, thanks for asking 👋")
            }
        }
    }

    it("checks that the routes for the recipe types are mapped with OPTIONS handler") {
        testApplication {
            application { setupTestServer() }
            val client = createClient { }

            with(client.options("/api/recipetype")) {
                shouldHaveHeader("Access-Control-Allow-Methods", "GET,POST,PUT")
            }
            with(client.options("/api/recipetype/123")) {
                shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
            }
        }
    }

    it("checks that the routes for the recipe are mapped with OPTIONS handler") {
        testApplication {
            application { setupTestServer() }
            val client = createClient { }

            with(client.options("/api/recipe")) {
                shouldHaveHeader("Access-Control-Allow-Methods", "GET,POST,PUT")
            }
            with(client.options("/api/recipe/123")) {
                shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
            }
            with(client.options("/api/recipe/123/photo")) {
                shouldHaveHeader("Access-Control-Allow-Methods", "POST")
            }
            with(client.options("/api/recipe/search")) {
                shouldHaveHeader("Access-Control-Allow-Methods", "POST")
            }
        }
    }
})
