package errors

open class ResourceNotFound(message: String? = null) : Exception(message ?: "Resource not found")
