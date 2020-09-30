package ports

import model.RecipeType

/** Defines the operations that are required to be done on the recipe type schema */
interface RecipeTypeRepository {
    /**
     * Finds a [RecipeType] in the database
     * @param id The recipe type's id
     * @return A [RecipeType] if found, null otherwise
     */
    fun find(id: Int): RecipeType?

    /**
     * Finds a [RecipeType] in the database
     * @param name The recipe type's name
     * @return A [RecipeType] if found, null otherwise
     */
    fun find(name: String): RecipeType?

    /**
     * Gets all the recipe types in the database
     * @return A list of [RecipeType]
     */
    fun getAll(): List<RecipeType>

    /** Returns the number of recipe type in the database */
    fun count(): Long

    /**
     * Creates a [RecipeType] in the database
     * @param recipeType The recipe type to be created
     * @return The created recipe type Id
     */
    fun create(recipeType: RecipeType): Int

    /**
     * Updates an existing [RecipeType] in the database
     * @param recipeType The recipe type to update
     */
    fun update(recipeType: RecipeType)

    /**
     * Deletes a [RecipeType] from the database
     * @param id The recipe type's Id
     * @return True if deleted, false otherwise
     */
    fun delete(id: Int): Boolean
}
