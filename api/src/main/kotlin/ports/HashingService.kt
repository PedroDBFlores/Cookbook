package ports

interface HashingService {
    fun hash(value: String): String
    fun verify(value: String, hash: String): Boolean
}
