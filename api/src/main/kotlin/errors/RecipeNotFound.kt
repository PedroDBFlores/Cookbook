package errors

import java.lang.Exception

class RecipeNotFound(identifier: Any) : Exception(identifier.toString())