package errors

class UserNotFound(userId: Int) : Exception("User with id $userId not found")