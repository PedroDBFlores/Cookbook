package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeDeleter

internal class DeleteRecipeTest : DescribeSpec({
    val intSource = Arb.int(1..100)

    it("deletes a recipe") {
        val recipeId = intSource.next()
        val recipeRepository = mockk<RecipeDeleter> {
            coEvery { this@mockk(recipeId) } returns true
        }
        val deleteRecipe = DeleteRecipe(recipeRepository)

        deleteRecipe(DeleteRecipe.Parameters(recipeId))

        coVerify(exactly = 1) {
            recipeRepository(recipeId)
        }
    }

    it("throws 'RecipeNotFound' if no rows were affected") {
        val recipeId = intSource.next()
        val recipeRepository = mockk<RecipeDeleter> {
            coEvery { this@mockk(recipeId) } returns false
        }
        val deleteRecipe = DeleteRecipe(recipeRepository)

        shouldThrow<RecipeNotFound> {
            deleteRecipe(DeleteRecipe.Parameters(recipeId))
        }

        coVerify(exactly = 1) { recipeRepository(any()) }
    }
})
