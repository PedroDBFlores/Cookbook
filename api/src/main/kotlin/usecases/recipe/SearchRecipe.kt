package usecases.recipe

import model.Recipe
import model.SearchResult
import ports.RecipeRepository

class SearchRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): SearchResult<Recipe> =
        recipeRepository.search(
            name = parameters.name,
            description = parameters.description,
            recipeTypeId = parameters.recipeTypeId,
            pageNumber = parameters.pageNumber,
            itemsPerPage = parameters.itemsPerPage
        )

    data class Parameters(
        val name: String? = null,
        val description: String? = null,
        val recipeTypeId: Int? = null,
        val pageNumber: Int = 1,
        val itemsPerPage: Int = 10
    )
}
