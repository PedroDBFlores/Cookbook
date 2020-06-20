package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import ports.RecipeTypeRepository
import utils.DTOGenerator

internal class GetRecipeTypeTest : DescribeSpec({
    val recipeTypeRepository = mockk<RecipeTypeRepository>()

    beforeTest {
        clearMocks(recipeTypeRepository)
    }

    describe("Get recipe type use case") {
        it("Gets a recipe type by id") {
            val expectedRecipeType = DTOGenerator.generateRecipeType()
            every { recipeTypeRepository.get(expectedRecipeType.id) } returns expectedRecipeType
            val getRecipeType = GetRecipeType(recipeTypeRepository)

            val recipeType = getRecipeType(GetRecipeType.Parameters(expectedRecipeType.id))

            recipeType.shouldBe(expectedRecipeType)
            verify(exactly = 1) { recipeTypeRepository.get(expectedRecipeType.id) }
        }

        it("throws if a recipe type is not found") {
            every { recipeTypeRepository.get(ofType()) } returns null
            val getRecipeType = GetRecipeType(recipeTypeRepository)

            val act = { getRecipeType(GetRecipeType.Parameters(1)) }

            shouldThrow<RecipeTypeNotFound> { act() }
            verify(exactly = 1) { recipeTypeRepository.get(any()) }
        }
    }
})