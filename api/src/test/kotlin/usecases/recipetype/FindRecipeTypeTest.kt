package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ports.RecipeTypeFinderById
import utils.recipeTypeGenerator

internal class FindRecipeTypeTest : DescribeSpec({
    it("Find a recipe type by id") {
        val basicRecipeType = recipeTypeGenerator.next()
        val recipeTypeFinderById = mockk<RecipeTypeFinderById> {
            coEvery { this@mockk(basicRecipeType.id) } returns basicRecipeType
        }
        val findRecipeType = FindRecipeType(recipeTypeFinderById)

        val recipeType = findRecipeType(FindRecipeType.Parameters(basicRecipeType.id))

        recipeType.shouldBe(basicRecipeType)
        coVerify(exactly = 1) { recipeTypeFinderById(basicRecipeType.id) }
    }

    it("throws if a recipe type is not found") {
        val recipeTypeId = Arb.int(1..100).next()
        val recipeTypeRepository = mockk<RecipeTypeFinderById> {
            coEvery { this@mockk(recipeTypeId) } returns null
        }
        val findRecipeType = FindRecipeType(recipeTypeRepository)

        shouldThrow<RecipeTypeNotFound> {
            findRecipeType(FindRecipeType.Parameters(recipeTypeId))
        }
        coVerify(exactly = 1) { recipeTypeRepository(recipeTypeId) }
    }
})
