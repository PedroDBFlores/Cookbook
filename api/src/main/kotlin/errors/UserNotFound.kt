package errors

class UserNotFound(private val id: Int? = null, private val userName: String? = null) : ResourceNotFound("") {
    private val notFoundMessage by lazy {
        if (id != null) "User with id $id not found" else "User with username $userName not found"
    }

    override val message: String?
        get() = notFoundMessage
}