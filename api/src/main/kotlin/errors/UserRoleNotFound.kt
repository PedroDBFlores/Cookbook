package errors

class UserRoleNotFound(userId: Int, roleId: Int) : ResourceNotFound("The role $roleId for user $userId was not found")
