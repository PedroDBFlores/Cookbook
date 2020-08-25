package usecases.recipe

import model.Recipe
import model.SearchResult
import model.parameters.SearchRecipeRequestBody
import ports.RecipeRepository

class SearchRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): SearchResult<Recipe> {
        val (searchParameters) = parameters

        return recipeRepository.search(searchParameters)
    }

    data class Parameters(val searchRequestBody: SearchRecipeRequestBody)
}
