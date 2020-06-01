import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import IntegrationTestUtils.getTestServer
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

internal class SwaggerITTest : DescribeSpec({

    describe("Swagger Test") {
        it("returns a json") {
            withTestApplication(getTestServer()) {
                handleRequest(HttpMethod.Get, "/openapi.json").apply {
                    response.status().shouldBe(HttpStatusCode.OK)
                    shouldNotThrow<JsonMappingException> {
                        ObjectMapper().readTree(response.content)
                    }
                }
            }
        }

        it("tests the swagger endpoint") {
            withTestApplication(getTestServer()) {
                handleRequest(HttpMethod.Get, "/").apply {
                    response.status().shouldBe(HttpStatusCode.MovedPermanently)
                }
            }
        }
    }
})
