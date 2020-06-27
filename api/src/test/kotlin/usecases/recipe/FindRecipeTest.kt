package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeRepository
import utils.DTOGenerator

class FindRecipeTest : DescribeSpec({
    describe("Find recipe use case") {
        it("Find a recipe by id") {
            val expectedRecipe = DTOGenerator.generateRecipe()
            val recipeRepository = mockk<RecipeRepository> {
                every { find(expectedRecipe.id) } returns expectedRecipe
            }
            val findRecipe = FindRecipe(recipeRepository)

            val recipe = findRecipe(FindRecipe.Parameters(expectedRecipe.id))

            recipe.shouldBe(expectedRecipe)
            verify(exactly = 1) { recipeRepository.find(expectedRecipe.id) }
        }

        it("throws if the recipe is not found") {
            val recipeRepository = mockk<RecipeRepository> {
                every { find(9999) } throws RecipeNotFound(9999)
            }
            val findRecipe = FindRecipe(recipeRepository)

            val act = { findRecipe(FindRecipe.Parameters(9999)) }

            shouldThrow<RecipeNotFound> { act() }
            verify(exactly = 1) { recipeRepository.find(any()) }
        }
    }
})