package errors

import java.lang.Exception

class RecipeTypeNotFound(identifier: Any) : Exception(identifier.toString())