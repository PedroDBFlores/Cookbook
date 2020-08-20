package ports

import io.ktor.application.*

interface KtorHandler {
    suspend fun handle(call: ApplicationCall)
}