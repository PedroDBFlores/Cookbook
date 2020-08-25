package model.parameters

import errors.ValidationError

data class SearchRecipeRequestBody(
    val name: String? = null,
    val description: String? = null,
    val recipeTypeId: Int? = null,
    val pageNumber: Int = 1,
    val itemsPerPage: Int = 10
) {
    init {
        recipeTypeId?.let { check(it > 0) { throw ValidationError("recipeTypeId") } }
        check(pageNumber > 0) { throw ValidationError("pageNumber") }
        check(itemsPerPage > 0) { throw ValidationError("itemsPerPage") }
    }
}
