package pt.pedro.pt.pedro.cookbook.exception.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import pt.pedro.cookbook.exception.service.ServiceException

internal class ServiceExceptionTest : DescribeSpec({
    describe("Service Exception") {
        it("has the intended message when no inner exception is provided") {
            val exception = ServiceException("ABC")

            exception.message.shouldBe("An error occurred on the ABC service")
        }

        it("has the intended message when an inner exception is provided") {
            val innerException = IllegalAccessError("Denied")
            val exception =
                ServiceException("ABC", innerException)

            exception.message.shouldBe("An error occurred on the ABC service: ${innerException.message}")
        }
    }
})