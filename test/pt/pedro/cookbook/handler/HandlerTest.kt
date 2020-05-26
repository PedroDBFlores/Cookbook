package pt.pedro.cookbook.handler

import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.mockk.coVerify
import io.mockk.spyk
import pt.pedro.cookbook.exception.service.EntityNotFoundException
import pt.pedro.cookbook.exception.service.ServiceException
import pt.pedro.utils.mocks.HandlerMocks.getApplicationCallMock

internal class HandlerTest : DescribeSpec({
    val handlerSpy = spyk<Handler>(recordPrivateCalls = true)

    describe("Handler tests") {
        it("sets a 404 error if an EntityNotFoundException is thrown") {
            val call = getApplicationCallMock()
            val exception = EntityNotFoundException("ABC", "Recipe", 1)

            handlerSpy.handleException(call, exception)

            coVerify {
                call.respond(HttpStatusCode.NotFound, exception.message!!)
            }
        }

        it("sets a 500 error on an general exception") {
            val call = getApplicationCallMock()
            val exception = ServiceException("ABC")

            handlerSpy.handleException(call, exception)

            coVerify {
                call.respond(HttpStatusCode.InternalServerError, exception.message!!)
            }
        }
    }
})