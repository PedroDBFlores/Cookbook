package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import ports.RecipeRepository
import utils.DTOGenerator

internal class UpdateRecipeTest : DescribeSpec({
    describe("Update recipe use case") {
        it("updates an existing recipe") {
            val currentRecipe = DTOGenerator.generateRecipe()
            val updatedRecipe = currentRecipe.copy(name = "NOT", description = "EQUAL")
            val recipeRepository = mockk<RecipeRepository> {
                every { update(updatedRecipe) } just runs
            }
            val updateRecipe = UpdateRecipe(recipeRepository)

            updateRecipe(updatedRecipe)

            verify(exactly = 1) {
                recipeRepository.update(updatedRecipe)
            }
        }

        it("throws if the recipe doesn't exist") {
            val recipeRepository = mockk<RecipeRepository> {
                every { update(any()) } throws RecipeNotFound(123)
            }
            val updateRecipe = UpdateRecipe(recipeRepository)

            val act = { updateRecipe(DTOGenerator.generateRecipe()) }

            shouldThrow<RecipeNotFound> { act() }
            verify(exactly = 1) { recipeRepository.update(any()) }
        }
    }
})
