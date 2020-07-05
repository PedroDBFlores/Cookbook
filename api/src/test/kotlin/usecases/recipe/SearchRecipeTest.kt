package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.SearchResult
import model.parameters.SearchRecipeParameters
import ports.RecipeRepository
import utils.DTOGenerator

internal class SearchRecipeTest : DescribeSpec({
    describe("Search recipes use case") {
        it("it searches for recipes in the database") {
            val expectedRecipes = List(21) {
                DTOGenerator.generateRecipe()
            }
            val searchParameters = SearchRecipeParameters(name = "Cake")
            val repo = mockk<RecipeRepository> {
                every { search(searchParameters) } returns SearchResult(1, 1, listOf(expectedRecipes.first()))
            }
            val searchRecipe = SearchRecipe(recipeRepository = repo)

            val searchResults = searchRecipe(searchParameters)

            with(searchResults) {
                count.shouldBe(1)
                numberOfPages.shouldBe(1)
                results.shouldBe(listOf(expectedRecipes.first()))
            }
            verify { repo.search(parameters = searchParameters) }
        }
    }
})
