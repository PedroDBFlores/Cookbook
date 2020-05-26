package pt.pedro.cookbook.exception.service

/**
 * Represents an exception that originated in the service layer
 * @param serviceName The name of the service where the original exception occurred
 * @param innerException The original exception
 */
internal open class ServiceException(
    protected open val serviceName: String,
    innerException: Throwable? = null
) :
    Exception(innerException) {

    override val message: String?
        get() {
            return if (this.cause == null)
                "An error occurred on the $serviceName service"
            else "An error occurred on the $serviceName service: ${cause!!.message}"
        }
}