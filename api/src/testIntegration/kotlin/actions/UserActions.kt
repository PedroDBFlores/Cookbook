package actions

import java.net.URI
import java.net.http.HttpResponse

object UserActions {
    suspend fun createUser(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(URI("$baseUrl/user"), requestBody)

    suspend fun loginUser(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(URI("$baseUrl/user/login"), requestBody)
}