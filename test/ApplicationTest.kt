package pt.pedro

import io.kotest.assertions.show.show
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import pt.pedro.cookbook.server.serverModule

internal class ApplicationTest : DescribeSpec({
    val configure: Application.() -> Unit = {
        (environment.config as MapApplicationConfig).apply {
        }
        println(environment.rootPath)
    }


    describe("Application Test") {
        it("tests the root endpoint") {
            withTestApplication(configure) {
                handleRequest(HttpMethod.Get, "/").apply {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content.shouldBe("HELLO WORLD!")
                }
            }
        }
    }
})
