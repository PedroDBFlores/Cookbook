package pt.pedro.cookbook.handler

import io.ktor.application.ApplicationCall

/**
 * Represents a simple interface for the handler that matches the CrudService interface
 */
interface CrudHandler {

    /**
     * Gets a entity
     */
    suspend fun get(call: ApplicationCall)

    /**
     * Gets all the entities
     * @return The list of entities
     */
    suspend fun getAll(call: ApplicationCall)

    /**
     * Creates the provided entity
     */
    suspend fun create(call: ApplicationCall)

    /**
     * Updates the provided entity
     */
    suspend fun update(call: ApplicationCall)

    /**
     * Deletes the entity
     */
    suspend fun delete(call: ApplicationCall)
}