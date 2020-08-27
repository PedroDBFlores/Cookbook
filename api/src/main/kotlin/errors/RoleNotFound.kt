package errors

class RoleNotFound(
    private val code: String? = null, private val id: Int? = null
) : Exception() {
    private val notFoundMessage by lazy {
        if (code != null) "Role with code '$code' not found" else "Role with id '$id' not found"
    }

    override val message: String?
        get() = notFoundMessage
}
