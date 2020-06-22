package errors

class ValidationError(field: String) : IllegalArgumentException("Field '$field' is invalid")