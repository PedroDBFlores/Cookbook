package flows

import kotlinx.coroutines.future.await
import utils.JsonHelpers
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object UserFlows {
    suspend fun createUser(baseUrl: String, requestBody: String): HttpResponse<String> {
        val createUserRequest = HttpRequest
            .newBuilder()
            .uri(URI("$baseUrl/user"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        return HttpClient.newHttpClient()
            .sendAsync(createUserRequest, HttpResponse.BodyHandlers.ofString()).await()
    }

    suspend fun loginUser(baseUrl: String, requestBody: String): HttpResponse<String> {
        val loginUserRequest = HttpRequest
            .newBuilder()
            .uri(URI("$baseUrl/user/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        return HttpClient.newHttpClient()
            .sendAsync(loginUserRequest, HttpResponse.BodyHandlers.ofString()).await()
    }
}