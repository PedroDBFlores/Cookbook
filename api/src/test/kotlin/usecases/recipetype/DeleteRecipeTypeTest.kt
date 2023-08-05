package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeTypeDeleter

internal class DeleteRecipeTypeTest : DescribeSpec({
    val intSource = Arb.int(1..100)

    it("deletes a recipe type") {
        val recipeTypeId = intSource.next()
        val recipeTypeRepository = mockk<RecipeTypeDeleter> {
            coEvery { this@mockk(recipeTypeId) } returns true
        }
        val deleteRecipeType = DeleteRecipeType(recipeTypeRepository)

        deleteRecipeType(DeleteRecipeType.Parameters(recipeTypeId))

        coVerify(exactly = 1) { recipeTypeRepository(recipeTypeId) }
    }

    it("throws 'RecipeTypeNotFound' if no rows were affected") {
        val recipeTypeId = intSource.next()
        val recipeTypeRepository = mockk<RecipeTypeDeleter> {
            coEvery { this@mockk(recipeTypeId) } throws RecipeTypeNotFound(recipeTypeId)
        }
        val deleteRecipeType = DeleteRecipeType(recipeTypeRepository)

        shouldThrow<RecipeTypeNotFound> {
            deleteRecipeType(DeleteRecipeType.Parameters(recipeTypeId))
        }
        coVerify(exactly = 1) { recipeTypeRepository(recipeTypeId) }
    }
})
