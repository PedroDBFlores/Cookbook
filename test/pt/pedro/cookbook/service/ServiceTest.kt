package pt.pedro.cookbook.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import pt.pedro.cookbook.exception.service.ServiceException

internal class ServiceTest : DescribeSpec({
    val serviceSpy = spyk<Service>(recordPrivateCalls = true)

    describe("Service tests") {
        it("it throws a ServiceError on a generic exception") {
            val exception = IllegalArgumentException("arg0")

            shouldThrow<ServiceException> {
                serviceSpy.handleException(exception)
            }
        }

        it("lets it through if it's already a ServiceException") {
            val expectedThrownException = ServiceException("ABC")

            val serviceException = shouldThrow<ServiceException> {
                serviceSpy.handleException(expectedThrownException)
            }

            serviceException.shouldBe(expectedThrownException)
        }
    }
})