package pt.pedro.cookbook.service

/**
 * Represents a simple interface for the service that matches the CrudRepository interface
 * @param T The entity type (DTO, not the table)
 */
interface CrudService<T> {

    /**
     * Gets a entity from the database
     * @id The entity's id
     * @return The entity if found, throws otherwise
     * @throws pt.pedro.cookbook.exception.service.EntityNotFoundException
     */
    suspend fun get(id: Int): T

    /**
     * Gets all the entities from a given table from the database
     * @return The list of entities
     */
    suspend fun getAll(): List<T>

    /**
     * Creates the provided entity
     * @param entity The entity to be created
     * @return The entity with the newly created id
     */
    suspend fun create(entity: T): T

    /**
     * Updates the provided entity
     * @param entity The entity to be updated
     * @return The updated entity
     */
    suspend fun update(entity: T): T

    /**
     * Deletes the entity
     * @param id The id of the entity to be deleted
     * @return True if deleted, false otherwise
     */
    suspend fun delete(id: Int): Boolean
}