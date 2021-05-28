package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeRepository

internal class DeleteRecipeTest : DescribeSpec({
    val intSource = Arb.int(1..100)

    it("deletes a recipe") {
        val recipeId = intSource.next()
        val recipeRepository = mockk<RecipeRepository> {
            every { delete(recipeId) } returns true
        }
        val deleteRecipe = DeleteRecipe(recipeRepository)

        deleteRecipe(DeleteRecipe.Parameters(recipeId))

        verify(exactly = 1) {
            recipeRepository.delete(recipeId)
        }
    }

    it("throws 'RecipeNotFound' if no rows were affected") {
        val recipeId = intSource.next()
        val recipeRepository = mockk<RecipeRepository> {
            every { delete(any()) } returns false
        }
        val deleteRecipe = DeleteRecipe(recipeRepository)

        val act = { deleteRecipe(DeleteRecipe.Parameters(recipeId)) }

        shouldThrow<RecipeNotFound>(act)
        verify(exactly = 1) { recipeRepository.delete(any()) }
    }
})
