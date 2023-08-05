package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.*
import ports.RecipeLister
import utils.recipeGenerator

internal class GetAllRecipesTest : DescribeSpec({
    val basicRecipe = recipeGenerator.next()

    it("gets all the recipes") {
        val expectedRecipes = listOf(
            basicRecipe.copy(),
            basicRecipe.copy(id = 2)
        )
        val recipeRepository = mockk<RecipeLister> {
            coEvery { this@mockk() } returns expectedRecipes
        }
        val getAllRecipes = GetAllRecipes(recipeRepository)

        val recipes = getAllRecipes()

        recipes.shouldBe(expectedRecipes)
        coVerify(exactly = 1) { recipeRepository() }
    }
})
