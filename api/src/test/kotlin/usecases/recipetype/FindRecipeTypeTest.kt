package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeTypeRepository
import utils.DTOGenerator

internal class FindRecipeTypeTest : DescribeSpec({
    describe("Find recipe type use case") {
        it("Find a recipe type by id") {
            val expectedRecipeType = DTOGenerator.generateRecipeType()
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(expectedRecipeType.id) } returns expectedRecipeType
            }
            val findRecipeType = FindRecipeType(recipeTypeRepository)

            val recipeType = findRecipeType(FindRecipeType.Parameters(expectedRecipeType.id))

            recipeType.shouldBe(expectedRecipeType)
            verify(exactly = 1) { recipeTypeRepository.find(expectedRecipeType.id) }
        }

        it("throws if a recipe type is not found") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(any()) } returns null
            }
            val findRecipeType = FindRecipeType(recipeTypeRepository)

            val act = { findRecipeType(FindRecipeType.Parameters(1)) }

            shouldThrow<RecipeTypeNotFound> { act() }
            verify(exactly = 1) { recipeTypeRepository.find(any()) }
        }
    }
})
