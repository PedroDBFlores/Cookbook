package errors

class UserRoleNotFound(userId: Int, roleId: Int) : Exception("The role $roleId for user $userId was not found")