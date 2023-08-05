package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeTypeFinderById
import ports.RecipeTypeUpdater
import utils.recipeTypeGenerator

internal class UpdateRecipeTypeTest : DescribeSpec({
    val currentRecipeType = recipeTypeGenerator.next()

    it("updates a recipe type") {
        val expectedRecipeType = currentRecipeType.copy(name = "Cake")
        val recipeTypeFinderById = mockk<RecipeTypeFinderById> {
            coEvery { this@mockk(expectedRecipeType.id) } returns currentRecipeType
        }
        val recipeTypeUpdater = mockk<RecipeTypeUpdater> {
            coEvery { this@mockk(expectedRecipeType) } just Runs
        }
        val updateRecipeType = UpdateRecipeType(recipeTypeFinderById, recipeTypeUpdater)

        updateRecipeType(
            UpdateRecipeType.Parameters(
                id = expectedRecipeType.id,
                name = expectedRecipeType.name
            )
        )

        coVerify(exactly = 1) {
            recipeTypeFinderById(expectedRecipeType.id)
            recipeTypeUpdater(expectedRecipeType)
        }
    }

    it("throws if the recipe type doesn't exist") {
        val recipeTypeFinderById = mockk<RecipeTypeFinderById> {
            coEvery { this@mockk(any()) } returns null
        }
        val recipeTypeUpdater = mockk<RecipeTypeUpdater>()
        val updateRecipeType = UpdateRecipeType(recipeTypeFinderById, recipeTypeUpdater)

        shouldThrow<RecipeTypeNotFound> {
            updateRecipeType(
                UpdateRecipeType.Parameters(
                    id = currentRecipeType.id,
                    name = "New recipe type"
                )
            )
        }

        coVerify { recipeTypeUpdater wasNot called }
    }
})
