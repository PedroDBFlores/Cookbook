package errors

import java.lang.Exception

open class ResourceAlreadyExists(errorMessage: String? = null) : Exception(errorMessage ?: "Resource already exists")