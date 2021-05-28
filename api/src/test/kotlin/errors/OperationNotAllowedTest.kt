package errors

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

internal class OperationNotAllowedTest : DescribeSpec({
    it("takes a message") {
        val message = Arb.string(16..64).next()

        val error = OperationNotAllowed(message)

        error.message.shouldBe(message)
    }
})
