package pt.pedro.cookbook.handler

import com.github.javafaker.Faker
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.mockk.*
import pt.pedro.cookbook.exception.service.ServiceException
import pt.pedro.cookbook.service.RecipeTypeService
import utils.DTOGenerator.generateRecipeType
import io.ktor.application.install
import io.ktor.http.HttpMethod
import io.ktor.routing.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import pt.pedro.cookbook.server.modules.jsonModule
import utils.convertToJSON

internal class RecipeTypeHandlerTest : DescribeSpec({
    val faker = Faker()
    val service = mockk<RecipeTypeService>()
    val handlerSpy = spyk(RecipeTypeHandler(service))

    beforeTest {
        clearAllMocks()
    }

    fun testServer(): Application.() -> Unit {
        return {
            jsonModule(true)
            install(Routing) {
                route("/api/recipetype") {
                    get("{id}") { handlerSpy.get(call) }
                    get { handlerSpy.getAll(call) }
                    post { handlerSpy.create(call) }
                    put { handlerSpy.update(call) }
                    delete("{id}") { handlerSpy.delete(call) }
                }
            }
        }
    }

    describe("Get by id") {
        it("gets a recipe type by id") {
            val recipeTypeId = faker.number().randomDigitNotZero()
            val expectedRecipeType = generateRecipeType(id = recipeTypeId)
            coEvery { service.get(recipeTypeId) } returns expectedRecipeType

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Get, "/api/recipetype/$recipeTypeId").apply {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content!!.shouldMatchJson(convertToJSON(expectedRecipeType))

                    coVerify {
                        service.get(recipeTypeId)
                    }
                }
            }
        }

        it("should return 400 if an invalid id is provided") {
            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Get, "/api/recipetype/arroz").apply {
                    response.status().shouldBe(HttpStatusCode.BadRequest)
                    response.content.shouldBe("The id provided is invalid")

                    coVerify {
                        service.get(ofType()) wasNot called
                    }
                }
            }
        }

        it("handles the exception from the service layer") {
            val exception = ServiceException("RecipeType")
            coEvery { service.get(1) } throws exception

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Get, "/api/recipetype/1").apply {
                    response.status().shouldBe(HttpStatusCode.InternalServerError)

                    coVerify {
                        service.get(1)
                        handlerSpy.handleException(ofType(), exception)
                    }
                }
            }
        }
    }

    describe("Get all") {
        it("returns all the recipe types") {
            val allRecipeTypes = listOf(
                generateRecipeType(),
                generateRecipeType()
            )
            coEvery { service.getAll() } returns allRecipeTypes

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Get, "/api/recipetype").apply {
                    response.status().shouldBe(HttpStatusCode.OK)
                    response.content!!.shouldMatchJson(convertToJSON(allRecipeTypes))

                    coVerify {
                        service.getAll()
                    }
                }
            }
        }

        it("handles the exception from the service layer") {
            val exception = ServiceException("RecipeType")
            coEvery { service.getAll() } throws exception

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Get, "/api/recipetype").apply {
                    response.status().shouldBe(HttpStatusCode.InternalServerError)

                    coVerify {
                        service.getAll()
                        handlerSpy.handleException(ofType(), exception)
                    }
                }
            }
        }
    }

    describe("Create") {
        it("creates a new user in the application") {
            val recipeTypeToCreate = generateRecipeType(id = 0)
            coEvery { service.create(recipeTypeToCreate) } returns recipeTypeToCreate.copy(id = 1)

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Post, "/api/recipetype") {
                    setBody(convertToJSON(recipeTypeToCreate))
                    addHeader("Content-Type", "application/json")
                }.apply {
                    response.status().shouldBe(HttpStatusCode.Created)

                    coVerify {
                        service.create(recipeTypeToCreate)
                    }
                }
            }
        }

        arrayOf(
            row(
                "",
                "no body is provided",
                "The provided JSON hasn't got the needed structure"
            ),
            row(
                """{"non":"conformant"}""",
                "an invalid body is provided",
                "The provided JSON hasn't got the needed structure"
            ),
            row(
                """{"name":""}""",
                "the name is empty",
                "Name cannot be empty"
            )
        ).forEach { (body: String, description: String, expectedMessage: String) ->
            it("returns 400 when $description") {
                withTestApplication(testServer()) {
                    handleRequest(HttpMethod.Post, "/api/recipetype") {
                        setBody(body)
                        addHeader("Content-Type", "application/json")
                    }.apply {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        response.content.shouldBe(expectedMessage)

                        coVerify {
                            service.create(ofType()) wasNot called
                        }
                    }
                }
            }
        }

        it("handles the exception from the service layer") {
            val recipeTypeToCreate = generateRecipeType(id = 0)
            val exception = ServiceException("RecipeType")
            coEvery { service.create(recipeTypeToCreate) } throws exception

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Post, "/api/recipetype") {
                    setBody(convertToJSON(recipeTypeToCreate))
                    addHeader("Content-Type", "application/json")
                }.apply {
                    response.status().shouldBe(HttpStatusCode.InternalServerError)

                    coVerify {
                        service.create(recipeTypeToCreate)
                        handlerSpy.handleException(ofType(), exception)
                    }
                }
            }
        }
    }

    describe("Update") {
        it("updates a recipe type in the application") {
            val recipeTypeToUpdate = generateRecipeType(name = faker.food().spice())
            coEvery { service.update(recipeTypeToUpdate) } returns recipeTypeToUpdate

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Put, "/api/recipetype") {
                    setBody(convertToJSON(recipeTypeToUpdate))
                    addHeader("Content-Type", "application/json")
                }.apply {
                    response.status().shouldBe(HttpStatusCode.OK)

                    coVerify {
                        service.update(recipeTypeToUpdate)
                    }
                }
            }
        }

        arrayOf(
            row(
                "",
                "no body is provided",
                "The provided JSON hasn't got the needed structure"
            ),
            row(
                """{"non":"conformant"}""",
                "an invalid body is provided",
                "The provided JSON hasn't got the needed structure"
            ),
            row(
                """{"id":""}""",
                "the name field is missing",
                "The provided JSON hasn't got the needed structure"
            ),
            row(
                """{"id":123,"name":""}""",
                "the name is empty",
                "Name cannot be empty"
            ),
            row(
                """{"id":0, "name":""}""",
                "the id is invalid",
                "Id cannot be 0"
            )
        ).forEach { (body: String, description: String, expectedMessage: String) ->
            it("returns 400 when $description") {
                withTestApplication(testServer()) {
                    handleRequest(HttpMethod.Put, "/api/recipetype") {
                        setBody(body)
                        addHeader("Content-Type", "application/json")
                    }.apply {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        response.content.shouldBe(expectedMessage)

                        coVerify {
                            service.update(ofType()) wasNot called
                        }
                    }
                }
            }
        }

        it("handles the exception from the service layer") {
            val recipeTypeToUpdate = generateRecipeType()
            val exception = ServiceException("RecipeType")
            coEvery { service.update(recipeTypeToUpdate) } throws exception

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Put, "/api/recipetype") {
                    setBody(convertToJSON(recipeTypeToUpdate))
                    addHeader("Content-Type", "application/json")
                }.apply {
                    response.status().shouldBe(HttpStatusCode.InternalServerError)

                    coVerify {
                        service.update(recipeTypeToUpdate)
                        handlerSpy.handleException(ofType(), exception)
                    }
                }
            }
        }
    }

    describe("Delete") {
        it("deletes a recipe type from the application") {
            val recipeTypeId = faker.number().randomDigitNotZero()
            coEvery { service.delete(recipeTypeId) } returns true

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Delete, "/api/recipetype/$recipeTypeId").apply {
                    response.status().shouldBe(HttpStatusCode.OK)

                    coVerify {
                        service.delete(recipeTypeId)
                    }
                }
            }
        }

        it("should return 400 if an invalid id is provided") {
            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Delete, "/api/recipetype/arroz").apply {
                    response.status().shouldBe(HttpStatusCode.BadRequest)
                    response.content.shouldBe("The id provided is invalid")

                    coVerify {
                        service.delete(ofType()) wasNot called
                    }
                }
            }
        }

        it("handles the exception from the service layer") {
            val exception = ServiceException("RecipeType")
            coEvery { service.delete(1) } throws exception

            withTestApplication(testServer()) {
                handleRequest(HttpMethod.Delete, "/api/recipetype/1").apply {
                    response.status().shouldBe(HttpStatusCode.InternalServerError)

                    coVerify {
                        service.delete(1)
                        handlerSpy.handleException(ofType(), exception)
                    }
                }
            }
        }
    }
})