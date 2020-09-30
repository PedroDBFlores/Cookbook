package adapters

import at.favre.lib.crypto.bcrypt.BCrypt
import ports.HashingService
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

/** A wrapper around the [BCrypt] implementation */
class BcryptHashingService : HashingService {
    override fun hash(value: String): String = String(
        BCrypt.with(SecureRandom()).hash(10, value.toByteArray()),
        StandardCharsets.UTF_8
    )

    override fun verify(value: String, hash: String) =
        BCrypt.verifyer(BCrypt.Version.VERSION_2A).verify(value.toCharArray(), hash.toCharArray()).verified
}
