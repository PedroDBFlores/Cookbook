package usecases.recipe

import model.Recipe
import model.SearchResult
import ports.RecipeRepository

class SearchRecipe(private val recipeRepository: RecipeRepository) {
    operator fun invoke(parameters: Parameters): SearchResult<Recipe> {
        val (name, description, recipeTypeId, pageNumber, itemsPerPage) = parameters

        return recipeRepository.search(
            name = name,
            description = description,
            recipeTypeId = recipeTypeId,
            pageNumber = pageNumber,
            itemsPerPage = itemsPerPage
        )
    }

    data class Parameters(
        val name: String? = null,
        val description: String? = null,
        val recipeTypeId: Int? = null,
        val pageNumber: Int = 1,
        val itemsPerPage: Int = 10
    )
}
