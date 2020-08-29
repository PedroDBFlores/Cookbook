package ports

import io.ktor.application.*

/**
 * Represents the contract that a Ktor handler on this application has to follow
 */
interface KtorHandler {
    /**
     * The function that has the handler behavior for that endpoint
     * @param call The current endpoint call
     */
    suspend fun handle(call: ApplicationCall)
}