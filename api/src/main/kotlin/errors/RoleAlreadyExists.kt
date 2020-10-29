package errors

class RoleAlreadyExists(code: String) :
    ResourceAlreadyExists("A role with the code $code already exists")
