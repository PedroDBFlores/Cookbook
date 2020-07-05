package errors

class RoleNotFound(code: String) : Exception("Role with code $code not found")
