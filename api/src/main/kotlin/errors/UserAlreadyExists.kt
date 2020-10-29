package errors

class UserAlreadyExists(userName: String) :
    ResourceAlreadyExists("An user with the username '$userName' already exists")
