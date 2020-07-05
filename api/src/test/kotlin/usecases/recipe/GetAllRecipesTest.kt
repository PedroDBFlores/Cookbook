package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ports.RecipeRepository
import utils.DTOGenerator

internal class GetAllRecipesTest : DescribeSpec({
    describe("Get all recipes use case") {
        it("gets all the recipes") {
            val expectedRecipes = listOf(
                DTOGenerator.generateRecipe(),
                DTOGenerator.generateRecipe()
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
