package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.RecipeType
import ports.RecipeTypeRepository

internal class FindRecipeTypeTest : DescribeSpec({
    describe("Find recipe type use case") {

        it("Find a recipe type by id") {
            val basicRecipeType = RecipeType(id = 1, name = "Recipe type")
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(basicRecipeType.id) } returns basicRecipeType
            }
            val findRecipeType = FindRecipeType(recipeTypeRepository)

            val recipeType = findRecipeType(FindRecipeType.Parameters(basicRecipeType.id))

            recipeType.shouldBe(basicRecipeType)
            verify(exactly = 1) { recipeTypeRepository.find(basicRecipeType.id) }
        }

        it("throws if a recipe type is not found") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { find(ofType<Int>()) } returns null
            }
            val findRecipeType = FindRecipeType(recipeTypeRepository)

            val act = { findRecipeType(FindRecipeType.Parameters(1)) }

            val recipeTypeNotFound = shouldThrow<RecipeTypeNotFound> (act)
            recipeTypeNotFound.message.shouldBe("Recipe type with id 1 not found")
            verify(exactly = 1) { recipeTypeRepository.find(ofType<Int>()) }
        }
    }
})
