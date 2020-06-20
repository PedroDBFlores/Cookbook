package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeRepository
import utils.DTOGenerator

internal class GetRecipeTest : DescribeSpec({
    val recipeRepository = mockk<RecipeRepository>()

    beforeTest {
        clearMocks(recipeRepository)
    }

    describe("Get recipe use case") {
        it("gets a recipe by id") {
            val expectedRecipe = DTOGenerator.generateRecipe()
            every { recipeRepository.get(expectedRecipe.id) } returns expectedRecipe
            val getRecipe = GetRecipe(recipeRepository)

            val recipe = getRecipe(GetRecipe.Parameters(expectedRecipe.id))

            recipe.shouldBe(expectedRecipe)
            verify(exactly = 1) { recipeRepository.get(expectedRecipe.id) }
        }

        it("throws if the recipe is not found") {
            every { recipeRepository.get(9999) } throws RecipeNotFound(9999)
            val getRecipe = GetRecipe(recipeRepository)

            val act = { getRecipe(GetRecipe.Parameters(9999)) }

            shouldThrow<RecipeNotFound> { act() }
            verify(exactly = 1) { recipeRepository.get(any()) }
        }
    }
})