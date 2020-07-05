package usecases.recipetype

import errors.RecipeTypeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeTypeRepository

internal class DeleteRecipeTypeTest : DescribeSpec({
    describe("Delete recipe type use case") {
        it("deletes a recipe type") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { delete(1) } returns true
            }
            val deleteRecipeType = DeleteRecipeType(recipeTypeRepository)

            deleteRecipeType(DeleteRecipeType.Parameters(1))

            verify(exactly = 1) { recipeTypeRepository.delete(1) }
        }

        it("throws if the recipe type doesn't exist") {
            val recipeTypeRepository = mockk<RecipeTypeRepository> {
                every { delete(any()) } throws RecipeTypeNotFound(1)
            }
            val deleteRecipeType = DeleteRecipeType(recipeTypeRepository)

            val act = { deleteRecipeType(DeleteRecipeType.Parameters(1)) }

            shouldThrow<RecipeTypeNotFound> { act() }
            verify(exactly = 1) { recipeTypeRepository.delete(any()) }
        }
    }
})
