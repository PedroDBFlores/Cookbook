package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.Recipe
import model.SearchResult
import ports.RecipeRepository

internal class SearchRecipeTest : DescribeSpec({
    describe("Search recipes use case") {
        it("it searches for recipes in the database") {
            val expectedRecipes = listOf(
                Recipe(
                    id = 0,
                    recipeTypeId = 1,
                    recipeTypeName = "Recipe type name",
                    userId = 1,
                    userName = "User name",
                    name = "Recipe Name",
                    description = "Recipe description",
                    ingredients = "Oh so many ingredients",
                    preparingSteps = "This will be so easy..."
                )
            )

            val repo = mockk<RecipeRepository> {
                every { search(name = "Cake", any(), any(), any(), any()) } returns SearchResult(
                    1,
                    1,
                    listOf(expectedRecipes.first())
                )
            }
            val searchRecipe = SearchRecipe(recipeRepository = repo)

            val searchResults = searchRecipe(SearchRecipe.Parameters(name = "Cake"))

            with(searchResults) {
                count.shouldBe(1)
                numberOfPages.shouldBe(1)
                results.shouldBe(listOf(expectedRecipes.first()))
            }
            verify { repo.search(name = "Cake", any(), any(), any(), any()) }
        }
    }
})
