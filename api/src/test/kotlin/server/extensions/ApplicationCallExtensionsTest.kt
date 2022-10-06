package server.extensions

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import server.modules.contentNegotiationModule
import utils.JsonHelpers.createJSONObject

@Serializable
data class ExampleClass(val id: Int, val name: String)

@Serializable
data class ExampleClassWithValidation(val id: Int, val name: String) {
    init {
        check(id < 100) { "Id must be at most 99" }
        check(name == "Marco") { "Name must be Marco" }
    }
}

internal class ApplicationCallExtensionsTest : DescribeSpec({

    describe("Receive or Throw") {

        describe("with custom validation") {
            val receivedBodyHandlerWithValidation: suspend ApplicationCall.() -> Unit = {
                val body = this.receiveOrThrow<ExampleClass> {
                    check(it.id == 1)
                    check(it.name == "Marco")
                }
                respond(HttpStatusCode.OK, body)
            }

            it("receives and returns the body as an object when it's successful") {
                val jsonBody = Json.encodeToString(ExampleClass(1, "Marco"))

                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(jsonBody)
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.OK)
                        response.content.shouldMatchJson(jsonBody)
                    }
                }
            }

            it("throws a BadRequestException if isn't able to transform the JSON") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(createJSONObject(mapOf("non" to "conforming")))
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }

            it("throws a BadRequestException when the validation fails") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandlerWithValidation(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(Json.encodeToString(ExampleClass(99, "Polo")))
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }
        }

        describe("no custom validation") {
            val receivedBodyHandler: suspend ApplicationCall.() -> Unit = {
                val body = this.receiveOrThrow<ExampleClassWithValidation>()
                respond(HttpStatusCode.OK, body)
            }

            it("receives and returns the body as an object when it's successful") {
                val jsonBody = Json.encodeToString(ExampleClassWithValidation(1, "Marco"))

                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandler(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(jsonBody)
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.OK)
                        response.content.shouldMatchJson(jsonBody)
                    }
                }
            }

            it("throws a BadRequestException if isn't able to transform the JSON") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandler(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(createJSONObject(mapOf("non" to "conforming")))
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }

            it("throws a BadRequestException if the init fails") {
                withTestApplication(
                    moduleFunction = {
                        contentNegotiationModule()
                        routing { post("*") { receivedBodyHandler(call) } }
                    }
                ) {
                    with(
                        handleRequest(HttpMethod.Post, "/something") {
                            setBody(
                                createJSONObject(
                                    mapOf(
                                        "id" to 100,
                                        "name" to "Polo"
                                    )
                                )
                            )
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }
})
