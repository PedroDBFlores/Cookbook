package pt.pedro.cookbook.domain.repository

import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents a simple CRUD repository operations for an Exposed table
 * @param T The entity type (DTO, not the table)
 */
internal interface CrudRepository<T> {
    /**
     * Gets a entity from the database
     * @id The entity's id
     * @return The entity if found, null otherwise
     */
    suspend fun get(id: Int): T?

    /**
     * Gets all the entities from a given table from the database
     * @return The list of entities
     */
    suspend fun getAll(): List<T>

    /**
     * Creates the provided entity in the database
     * @param entity The entity to be created
     * @return The entity with the newly created id
     */
    suspend fun create(entity: T): T

    /**
     * Updates the provided entity in the database
     * @param entity The entity to be updated
     * @return The updated entity
     */
    suspend fun update(entity: T): T

    /**
     * Deletes the entity from the database
     * @param id The id of the entity to be deleted
     * @return True if deleted, false otherwise
     */
    suspend fun delete(id: Int): Boolean

    /**
     * Maps the Exposed's result row to an entity
     * @param row The provided [ResultRow]
     * @return A mapped entity
     */
    fun mapToResult(row: ResultRow): T
}