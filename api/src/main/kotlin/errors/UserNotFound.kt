package errors

class UserNotFound(private val userId: Int?, private val userName: String? = null) : Exception() {
    private val notFoundMessage by lazy {
        if (userId != null) "User with id $userId not found" else "User with username $userName not found"
    }

    override val message: String?
        get() = notFoundMessage
}