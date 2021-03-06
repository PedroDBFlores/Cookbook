package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import ports.RecipeRepository

internal class GetAllRecipesTest : DescribeSpec({
    describe("Get all recipes use case") {
        val basicRecipe = Recipe(
            id = 1,
            recipeTypeId = 1,
            recipeTypeName = "Recipe type name",
            name = "Recipe Name",
            description = "Recipe description",
            ingredients = "Oh so many ingredients",
            preparingSteps = "This will be so easy..."
        )

        it("gets all the recipes") {
            val expectedRecipes = listOf(
                basicRecipe.copy(),
                basicRecipe.copy(id = 2)
            )
            val recipeRepository = mockk<RecipeRepository> {
                every { getAll() } returns expectedRecipes
            }
            val getAllRecipes = GetAllRecipes(recipeRepository)

            val recipes = getAllRecipes()

            recipes.shouldBe(expectedRecipes)
            verify(exactly = 1) { recipeRepository.getAll() }
        }
    }
})
