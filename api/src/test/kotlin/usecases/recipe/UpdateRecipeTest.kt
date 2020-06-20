package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import ports.RecipeRepository
import utils.DTOGenerator

internal class UpdateRecipeTest : DescribeSpec({
    val recipeRepository = mockk<RecipeRepository>()

    beforeTest {
        clearMocks(recipeRepository)
    }

    describe("Update recipe use case") {
        it("updates an existing recipe") {
            val currentRecipe = DTOGenerator.generateRecipe()
            val updatedRecipe = currentRecipe.copy(name = "NOT", description = "EQUAL")
            every { recipeRepository.get(currentRecipe.id) } returns currentRecipe
            every { recipeRepository.update(updatedRecipe) } just runs
            val updateRecipe = UpdateRecipe(recipeRepository)

            updateRecipe(updatedRecipe)

            verify(exactly = 1) {
                recipeRepository.get(currentRecipe.id)
                recipeRepository.update(updatedRecipe)
            }
        }

        it("throws if the recipe doesn't exist") {
            every { recipeRepository.get(any()) } returns null
            val updateRecipe = UpdateRecipe(recipeRepository)

            val act = { updateRecipe(DTOGenerator.generateRecipe()) }

            shouldThrow<RecipeNotFound> { act() }
            verify(exactly = 1) { recipeRepository.get(any()) }
            verify(exactly = 0) { recipeRepository.update(any()) }
        }
    }
})