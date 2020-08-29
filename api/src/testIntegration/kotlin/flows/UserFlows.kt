package flows

import java.net.URI
import java.net.http.HttpResponse

object UserFlows {
    suspend fun createUser(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(URI("$baseUrl/user"), requestBody)

    suspend fun loginUser(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(URI("$baseUrl/user/login"), requestBody)
}