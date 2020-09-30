package ports

/** Defines an interface for an hashing service */
interface HashingService {
    /**
     * Hashes the provided value
     * @return The hashed value
     */
    fun hash(value: String): String

    /**
     * Takes a value and a hash, and checks if the hashed value would become the provided hash
     * @param value The value to check
     * @param hash The expected hash
     * @return True if they match, false otherwise
     */
    fun verify(value: String, hash: String): Boolean
}
