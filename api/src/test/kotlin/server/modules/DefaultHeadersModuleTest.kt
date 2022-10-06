package server.modules

import io.kotest.assertions.ktor.client.shouldHaveHeader
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*

class DefaultHeadersModuleTest : DescribeSpec({
    it("adds a set of default headers to the response") {
        testApplication {
            application {
                defaultHeadersModule()
            }
            routing {
                get("*") { call.respond("OK") }
            }

            val client = createClient { }

            with(client.get("/route")) {
                this shouldHaveStatus HttpStatusCode.OK
                this.status.shouldBe(HttpStatusCode.OK)
                headers["Date"].shouldNotBeEmpty()
                shouldHaveHeader("Server", "Cookbook Server")
                shouldHaveHeader("X-CreatedBy", "Mr. Flowers")
            }
        }
    }
})
