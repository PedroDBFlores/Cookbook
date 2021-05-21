package ports

import model.Recipe
import model.SearchResult

/** Defines the operations that are required to be done on the recipe schema */
interface RecipeRepository {
    /**
     * Finds a [Recipe] in the database
     * @param id The recipe's id
     * @return A [Recipe] if found, null otherwise
     */
    fun find(id: Int): Recipe?

    /**
     * Gets all the recipes in the database
     * @return A list of [Recipe]
     */
    fun getAll(): List<Recipe>

    /** Returns the number of recipes in the database */
    fun count(): Long

    /**
     * Searches in the database by the provided parameters
     * @param name The recipe's partial or full name
     * @param description The recipe's partial or full description
     * @param recipeTypeId The recipe's recipe type
     * @param pageNumber The page from the database to fetch
     * @param itemsPerPage The number of elements to return
     * @return A [SearchResult] with the matching recipes
     */
    fun search(
        name: String?,
        description: String?,
        recipeTypeId: Int?,
        pageNumber: Int,
        itemsPerPage: Int
    ): SearchResult<Recipe>

    /**
     * Creates a [Recipe] in the database
     * @param recipe The recipe to be created
     * @return The created recipe Id
     */
    fun create(recipe: Recipe): Int

    /**
     * Updates an existing [Recipe] in the database
     * @param recipe The recipe to update
     */
    fun update(recipe: Recipe)

    /**
     * Deletes a [Recipe] from the database
     * @param id The recipe's Id
     * @return True if deleted, false otherwise
     */
    fun delete(id: Int): Boolean

    fun <T> runWrappedInTransaction(action: () -> T): T
}
