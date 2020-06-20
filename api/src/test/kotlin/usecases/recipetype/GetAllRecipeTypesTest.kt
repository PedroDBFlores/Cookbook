package usecases.recipetype

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import ports.RecipeTypeRepository
import utils.DTOGenerator.generateRecipeType

internal class GetAllRecipeTypesTest : DescribeSpec({
    val recipeTypeRepository = mockk<RecipeTypeRepository>()

    beforeTest {
        clearMocks(recipeTypeRepository)
    }

    describe("Get all recipe types use case") {
        it("gets all the recipe types") {
            val expectedRecipeTypes = listOf(
                generateRecipeType(),
                generateRecipeType()
            )
            every { recipeTypeRepository.getAll() } returns expectedRecipeTypes
            val getAllRecipeTypes = GetAllRecipeTypes(recipeTypeRepository)

            val recipeTypes = getAllRecipeTypes()

            recipeTypes.shouldBe(expectedRecipeTypes)
            verify(exactly = 1) { recipeTypeRepository.getAll() }
        }
    }
})