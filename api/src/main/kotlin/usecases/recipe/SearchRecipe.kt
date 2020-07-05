package usecases.recipe

import model.Recipe
import model.SearchResult
import model.parameters.SearchRecipeParameters
import ports.RecipeRepository

class SearchRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(searchParameters: SearchRecipeParameters): SearchResult<Recipe> {
        return recipeRepository.search(searchParameters)
    }
}
