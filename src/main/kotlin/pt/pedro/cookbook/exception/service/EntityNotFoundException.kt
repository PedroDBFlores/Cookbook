package pt.pedro.cookbook.exception.service

internal class EntityNotFoundException(serviceName: String, private val entityName: String, private val id: Int) :
    ServiceException(serviceName) {
    override val message: String?
        get() = "Couldn't get the $entityName with id $id from the $serviceName service"
}