package ports

import model.RecipeType

/** Finds a [RecipeType] by id */
fun interface RecipeTypeFinderById {
    /**
     * @param id The recipe type's id
     * @return A [RecipeType] if found, null otherwise
     */
    suspend operator fun invoke(id: Int): RecipeType?
}

/** Finds a [RecipeType] by name */
fun interface RecipeTypeFinderByName {
    /**
     * @param name The recipe type's name
     * @return A [RecipeType] if found, null otherwise
     */
    suspend operator fun invoke(name: String): RecipeType?
}

/** Gets all the recipe tpyes */
fun interface RecipeTypeLister {
    suspend operator fun invoke(): List<RecipeType>
}

/** Returns the number of recipe types*/
fun interface RecipeTypeCounter {
    suspend operator fun invoke(): Long
}

/** Creates a recipe type */
fun interface RecipeTypeCreator {
    /**
     * @param recipeType The recipe type to be created
     * @return The created recipe type id
     */
    suspend operator fun invoke(recipeType: RecipeType): Int
}

/** Updates a recipe type */
fun interface RecipeTypeUpdater {
    /** @param recipeType The recipe type to update */
    suspend operator fun invoke(recipeType: RecipeType)
}

/** Deletes a recipe type */
fun interface RecipeTypeDeleter {
    /**
     * @param id The recipe type's id
     * @return True if deleted, false otherwise
     */
    suspend operator fun invoke(id: Int): Boolean
}
