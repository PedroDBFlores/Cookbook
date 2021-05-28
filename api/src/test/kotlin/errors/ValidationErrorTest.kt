package errors

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.stringPattern

internal class ValidationErrorTest : DescribeSpec({
    it("has the name of the field in the message") {
        val stringSource = Arb.stringPattern("[0-9]([a-c]|[e-g]{1,2})")
        val fieldName = stringSource.next()

        val error = ValidationError(field = fieldName)

        error.message.shouldBe("Field '$fieldName' is invalid")
    }
})
