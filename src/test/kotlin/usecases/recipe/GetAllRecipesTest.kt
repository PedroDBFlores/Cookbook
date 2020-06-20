package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import ports.RecipeRepository
import utils.DTOGenerator

internal class GetAllRecipesTest : DescribeSpec({
    val recipeRepository = mockk<RecipeRepository>()

    beforeTest {
        clearMocks(recipeRepository)
    }

    describe("Get all recipes use case") {
        it("gets all the recipes") {
            val expectedRecipes = listOf(
                DTOGenerator.generateRecipe(),
                DTOGenerator.generateRecipe()
            )
            every { recipeRepository.getAll() } returns expectedRecipes
            val getAllRecipes = GetAllRecipes(recipeRepository)

            val recipes = getAllRecipes()

            recipes.shouldBe(expectedRecipes)
            verify(exactly = 1) { recipeRepository.getAll() }
        }
    }
})