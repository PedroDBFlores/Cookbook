package usecases.recipe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.SearchResult
import ports.RecipeRepository
import utils.recipeGenerator
import utils.searchRecipeParametersGenerator

internal class SearchRecipeTest : DescribeSpec({
    it("it searches for recipe in the database") {
        val expectedRecipes = listOf(recipeGenerator.next())
        val expectedSearchResult = SearchResult(
            1,
            1,
            listOf(expectedRecipes.first())
        )
        val searchParameters = searchRecipeParametersGenerator.next()
        val repo = mockk<RecipeRepository> {
            every {
                search(
                    name = searchParameters.name,
                    description = searchParameters.description,
                    recipeTypeId = searchParameters.recipeTypeId,
                    pageNumber = searchParameters.pageNumber,
                    itemsPerPage = searchParameters.itemsPerPage
                )
            } returns expectedSearchResult
        }
        val searchRecipe = SearchRecipe(recipeRepository = repo)

        val searchResults = searchRecipe(searchParameters)

        searchResults.shouldBe(expectedSearchResult)
        verify {
            repo.search(
                name = searchParameters.name,
                description = searchParameters.description,
                recipeTypeId = searchParameters.recipeTypeId,
                pageNumber = searchParameters.pageNumber,
                itemsPerPage = searchParameters.itemsPerPage
            )
        }
    }
})
