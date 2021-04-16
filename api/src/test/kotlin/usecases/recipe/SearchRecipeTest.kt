package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.SearchResult
import ports.RecipeRepository
import utils.recipeGenerator

internal class SearchRecipeTest : DescribeSpec({
    describe("Search recipes use case") {
        it("it searches for recipes in the database") {
            val expectedRecipes = listOf(recipeGenerator.next())
            val nameParameter = Arb.string(16).next()

            val repo = mockk<RecipeRepository> {
                every { search(name = nameParameter, any(), any(), any(), any()) } returns SearchResult(
                    1,
                    1,
                    listOf(expectedRecipes.first())
                )
            }
            val searchRecipe = SearchRecipe(recipeRepository = repo)

            val searchResults = searchRecipe(SearchRecipe.Parameters(name = nameParameter))

            with(searchResults) {
                count.shouldBe(1)
                numberOfPages.shouldBe(1)
                results.shouldBe(listOf(expectedRecipes.first()))
            }
            verify { repo.search(name = nameParameter, any(), any(), any(), any()) }
        }
    }
})
