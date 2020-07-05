package errors

class PasswordMismatchError : Exception("The provided password doesn't match the one in the database")
