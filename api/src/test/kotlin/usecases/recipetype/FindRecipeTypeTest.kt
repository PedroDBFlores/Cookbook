package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeTypeRepository
import utils.recipeTypeGenerator

internal class FindRecipeTypeTest : DescribeSpec({
    it("Find a recipe type by id") {
        val basicRecipeType = recipeTypeGenerator.next()
        val recipeTypeRepository = mockk<RecipeTypeRepository> {
            every { find(basicRecipeType.id) } returns basicRecipeType
        }
        val findRecipeType = FindRecipeType(recipeTypeRepository)

        val recipeType = findRecipeType(FindRecipeType.Parameters(basicRecipeType.id))

        recipeType.shouldBe(basicRecipeType)
        verify(exactly = 1) { recipeTypeRepository.find(basicRecipeType.id) }
    }

    it("throws if a recipe type is not found") {
        val recipeTypeId = Arb.int(1..100).next()
        val recipeTypeRepository = mockk<RecipeTypeRepository> {
            every { find(recipeTypeId) } returns null
        }
        val findRecipeType = FindRecipeType(recipeTypeRepository)

        val act = { findRecipeType(FindRecipeType.Parameters(recipeTypeId)) }

        shouldThrow<RecipeTypeNotFound>(act)
        verify(exactly = 1) { recipeTypeRepository.find(recipeTypeId) }
    }
})
