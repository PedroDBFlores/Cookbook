package web.recipetype

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import server.modules.contentNegotiationModule
import usecases.recipetype.GetAllRecipeTypes
import utils.DTOGenerator.generateRecipeType
import utils.convertToJSON

internal class GetAllRecipeTypesHandlerTest : DescribeSpec({

    fun createTestServer(getAllRecipeTypes: GetAllRecipeTypes): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            get("/api/recipetype") { GetAllRecipeTypesHandler(getAllRecipeTypes).handle(call) }
        }
    }

    describe("Get all recipe types handler") {
        val expectedRecipeTypes = listOf(generateRecipeType(), generateRecipeType())
        val getAllRecipeTypesMock = mockk<GetAllRecipeTypes> {
            every { this@mockk() } returns expectedRecipeTypes
        }

        withTestApplication(moduleFunction = createTestServer(getAllRecipeTypesMock)) {
            with(handleRequest(HttpMethod.Get, "/api/recipetype")) {
                response.status().shouldBe(HttpStatusCode.OK)
                response.content.shouldMatchJson(convertToJSON(expectedRecipeTypes))
                verify(exactly = 1) { getAllRecipeTypesMock() }
            }
        }
    }
})
