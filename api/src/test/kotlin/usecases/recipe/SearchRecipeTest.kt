package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import model.SearchResult
import ports.RecipeSearcher
import utils.recipeGenerator
import utils.searchRecipeParametersGenerator

internal class SearchRecipeTest : DescribeSpec({
    it("it searches for recipes") {
        val expectedRecipes = listOf(recipeGenerator.next())
        val expectedSearchResult = SearchResult(
            1,
            1,
            listOf(expectedRecipes.first())
        )
        val searchParameters = searchRecipeParametersGenerator.next()
        val recipeSearcher = mockk<RecipeSearcher> {
            coEvery {
                this@mockk(
                    name = searchParameters.name,
                    description = searchParameters.description,
                    recipeTypeId = searchParameters.recipeTypeId,
                    pageNumber = searchParameters.pageNumber,
                    itemsPerPage = searchParameters.itemsPerPage
                )
            } returns expectedSearchResult
        }
        val searchRecipe = SearchRecipe(recipeSearcher = recipeSearcher)

        val searchResults = searchRecipe(searchParameters)

        searchResults.shouldBe(expectedSearchResult)
        coVerify {
            recipeSearcher(
                name = searchParameters.name,
                description = searchParameters.description,
                recipeTypeId = searchParameters.recipeTypeId,
                pageNumber = searchParameters.pageNumber,
                itemsPerPage = searchParameters.itemsPerPage
            )
        }
    }
})
