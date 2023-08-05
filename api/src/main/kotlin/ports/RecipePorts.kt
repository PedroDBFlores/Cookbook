package ports

import model.Recipe
import model.SearchResult

/** Finds a recipe */
fun interface RecipeFinder {
    /**
     * @param id The recipe's id
     * @return A [Recipe] if found, null otherwise
     */
    suspend operator fun invoke(id: Int): Recipe?
}

/** Gets all the recipes */
fun interface RecipeLister {
    suspend operator fun invoke(): List<Recipe>
}

/** Returns the number of recipes */
fun interface RecipeCounter {
    suspend operator fun invoke(): Long
}

/** Searches for recipes with the provided parameters */
fun interface RecipeSearcher {
    /**
     * @param name The recipe's partial or full name
     * @param description The recipe's partial or full description
     * @param recipeTypeId The recipe's recipe type
     * @param pageNumber The page from the database to fetch
     * @param itemsPerPage The number of elements to return
     * @return A [SearchResult] with the matching recipes
     */
    suspend operator fun invoke(
        name: String?,
        description: String?,
        recipeTypeId: Int?,
        pageNumber: Int,
        itemsPerPage: Int
    ): SearchResult<Recipe>
}

/** Creates a recipe */
fun interface RecipeCreator {
    /**
     * @param recipe The recipe to be created
     * @return The created recipe id
     */
    suspend operator fun invoke(recipe: Recipe): Int
}

/** Updates a recipe */
fun interface RecipeUpdater {
    /** @param recipe The recipe to update */
    suspend operator fun invoke(recipe: Recipe)
}

/** Deletes a recipe */
fun interface RecipeDeleter {
    /**
     * @param id The recipe's id
     * @return True if deleted, false otherwise
     */
    suspend operator fun invoke(id: Int): Boolean
}
