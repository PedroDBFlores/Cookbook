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
            val recipeToCreate = Recipe(
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
                every { create(recipeToCreate) } returns 1
            }

            val createRecipe = CreateRecipe(recipeRepository)
            val recipeId = createRecipe(CreateRecipe.Parameters(recipeToCreate))

            recipeId.shouldBe(1)
            verify(exactly = 1) { recipeRepository.create(recipeToCreate) }
        }
    }
})
