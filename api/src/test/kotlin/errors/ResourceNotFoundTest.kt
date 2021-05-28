package errors

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

internal class ResourceNotFoundTest : DescribeSpec({
    val stringSource = Arb.string(16..32)

    it("has the default message") {
        val error = ResourceNotFound()

        error.message.shouldBe("Resource not found")
    }

    it("has it's own message") {
        val message = stringSource.next()
        val error = ResourceNotFound(message)

        error.message.shouldBe(message)
    }
})
