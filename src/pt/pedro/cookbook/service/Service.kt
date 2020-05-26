package pt.pedro.cookbook.service

import pt.pedro.cookbook.exception.service.ServiceException

/**
 * Base class for a service
 */
internal abstract class Service {
    /**
     * Handles the errors from the repository layer and/or from the service
     * @param ex The thrown exception
     */
    fun handleException(ex: Exception) : Nothing {
        if (ex is ServiceException) {
            throw ex
        }
        throw ServiceException(this.javaClass.simpleName, ex)
    }
}