package pt.pedro.cookbook.handler

import com.github.javafaker.Faker
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.mockk.*
import pt.pedro.cookbook.exception.service.ServiceException
import pt.pedro.cookbook.service.RecipeTypeService
import pt.pedro.utils.DTOGenerator.generateRecipeType
import pt.pedro.utils.mocks.HandlerMocks.getApplicationCallMock

internal class RecipeTypeHandlerTest : DescribeSpec({
    val faker = Faker()
    val recipeTypeService = mockk<RecipeTypeService>()

    beforeSpec {
        clearMocks(recipeTypeService)
    }

    describe("Get by id") {
        it("gets a recipe type by id") {
            val call = getApplicationCallMock()
            val recipeTypeId = faker.number().randomDigitNotZero()
            val expectedRecipeType = generateRecipeType(id = recipeTypeId)
            every { call.parameters["id"] } returns recipeTypeId.toString()
            coEvery { recipeTypeService.get(recipeTypeId) } returns expectedRecipeType

            val handler = RecipeTypeHandler(recipeTypeService)
            handler.get(call)

            coVerify {
                recipeTypeService.get(recipeTypeId)
                call.respond(HttpStatusCode.OK, expectedRecipeType)
            }
        }

        it("should return 400 if the id isn't provided") {
            val call = getApplicationCallMock()
            every { call.parameters["id"] } returns null

            val handler = RecipeTypeHandler(recipeTypeService)
            handler.get(call)

            coVerify {
                call.respond(HttpStatusCode.BadRequest, "Id is required")
                recipeTypeService.get(ofType()) wasNot called
            }
        }

        it("handles the exception from the service layer") {
            val call = getApplicationCallMock()
            val exception = ServiceException("RecipeType")
            every { call.parameters["id"] } returns "1"
            coEvery { recipeTypeService.get(1) } throws exception

            val handler = spyk(RecipeTypeHandler(recipeTypeService))
            handler.get(call)

            coVerify {
                recipeTypeService.get(1)
                handler.handleException(call, exception)
            }
        }
    }
})