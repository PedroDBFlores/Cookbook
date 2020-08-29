package flows

import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object RecipeTypeFlows {
    suspend fun getRecipeTypes(baseUrl: String, jwtToken: String? = null): HttpResponse<String> {
        val createUserRequestBuilder = HttpRequest
            .newBuilder()
            .uri(URI("$baseUrl/recipetype"))
            .GET()

        jwtToken?.let { createUserRequestBuilder.header("Authorization", "Bearer $jwtToken") }

        return HttpClient.newHttpClient()
            .sendAsync(createUserRequestBuilder.build(), HttpResponse.BodyHandlers.ofString()).await()
    }
}