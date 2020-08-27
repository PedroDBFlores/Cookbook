package usecases.recipe

import errors.RecipeNotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import ports.RecipeRepository

internal class FindRecipeTest : DescribeSpec({
    describe("Find recipe use case") {
        it("Find a recipe by id") {
            val expectedRecipe = Recipe(
                id = 1,
                recipeTypeId = 1,
                recipeTypeName = "Recipe type name",
                userId = 1,
                userName = "User name",
                name = "Recipe Name",
                description = "Recipe description",
                ingredients = "Oh so many ingredients",
                preparingSteps = "This will be so easy..."
            )
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

            val recipeNotFound = shouldThrow<RecipeNotFound> (act)
            recipeNotFound.message.shouldBe("Recipe with id 9999 not found")
            verify(exactly = 1) { recipeRepository.find(any()) }
        }
    }
})
