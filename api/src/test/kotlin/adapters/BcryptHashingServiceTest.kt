package adapters

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.string.shouldStartWith

internal class BcryptHashingServiceTest : DescribeSpec({
    describe("Bcrypt hashing service") {
        it("hashes a value") {
            val valueToBeHashed = "VALUE"

            val hashingService = BcryptHashingService()
            val hash = hashingService.hash(valueToBeHashed)
            hash.shouldStartWith("""${'$'}2a${'$'}10${'$'}""")
        }

        it("verifies an matching hash successfully") {
            val valueToMatch = "VALUE"
            val hashToMatch =
                """${'$'}2a${'$'}04${'$'}UBfKKQCyLTPXZUCyHBKvM.xpXpW.upzTAL14llmp92jLLjZBcoWzy"""

            val hashingService = BcryptHashingService()
            val hashMatches = hashingService.verify(valueToMatch, hashToMatch)

            hashMatches.shouldBeTrue()
        }

        it("verifies an hash that doesn't match") {
            val valueToMatch = "TOUCHE"
            val hashToMatch =
                """${'$'}2a${'$'}04${'$'}UBfKKQCyLTPXZUCyHBKvM.xpXpW.upzTAL14llmp92jLLjZBcoWzy"""

            val hashingService = BcryptHashingService()
            val hashMatches = hashingService.verify(valueToMatch, hashToMatch)

            hashMatches.shouldBeFalse()
        }
    }
})
