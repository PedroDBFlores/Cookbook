package pt.pedro.utils.mocks

import io.ktor.application.ApplicationCall
import io.mockk.mockk

/**
 * Provides mocks for using in the handler unit tests
 */
internal object HandlerMocks {
    /**
     * Gets a mock of the handler application call
     */
    fun getApplicationCallMock(): ApplicationCall {
        val call = mockk<ApplicationCall>(relaxed = true)
        return call
    }
}