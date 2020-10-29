package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import ports.RecipeRepository

internal class CreateRecipeTest : DescribeSpec({

    describe("Create recipe use case") {
        it("creates a recipe") {
            val expectedRecipe = Recipe(
                recipeTypeId = 1,
                userId = 1,
                name = "Recipe Name",
                description = "Recipe description",
                ingredients = "Oh so many ingredients",
                preparingSteps = "This will be so easy..."
            )
            val recipeRepository = mockk<RecipeRepository> {
                every { create(expectedRecipe) } returns 1
            }

            val createRecipe = CreateRecipe(recipeRepository)
            val recipeId = createRecipe(
                CreateRecipe.Parameters(
                    recipeTypeId = 1,
                    userId = 1,
                    name = "Recipe Name",
                    description = "Recipe description",
                    ingredients = "Oh so many ingredients",
                    preparingSteps = "This will be so easy..."
                )
            )

            recipeId.shouldBe(1)
            verify(exactly = 1) { recipeRepository.create(expectedRecipe) }
        }
    }
})
