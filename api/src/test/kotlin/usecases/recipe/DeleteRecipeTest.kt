package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeRepository

class DeleteRecipeTest : DescribeSpec({
    describe("Delete recipe use case") {
        it("deletes a recipe") {
            val recipeRepository = mockk<RecipeRepository> {
                every { delete(1) } returns true
            }
            val deleteRecipe = DeleteRecipe(recipeRepository)

            deleteRecipe(DeleteRecipe.Parameters(1))

            verify(exactly = 1) {
                recipeRepository.delete(1)
            }
        }

        it("throws if a recipe doesn't exist") {
            val recipeRepository = mockk<RecipeRepository> {
                every { delete(any()) } returns false
            }
            val deleteRecipe = DeleteRecipe(recipeRepository)

            val act = { deleteRecipe(DeleteRecipe.Parameters(1)) }

            shouldThrow<RecipeNotFound> { act() }
            verify(exactly = 1) { recipeRepository.delete(any()) }
        }
    }
})