package actions

import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private fun provideHttpRequestBuilder(uri: URI, vararg headers: Pair<String, String>): HttpRequest.Builder {
    val requestBuilder = HttpRequest
        .newBuilder()
        .uri(uri)

    headers.forEach { requestBuilder.header(it.first, it.second) }
    return requestBuilder
}

suspend fun executeGETRequest(uri: URI, vararg headers: Pair<String, String>): HttpResponse<String> {
    val requestBuilder = provideHttpRequestBuilder(uri, *headers)
        .GET()

    return HttpClient.newHttpClient()
        .sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).await()
}

suspend fun executePOSTRequest(
    uri: URI,
    requestBody: String,
    vararg headers: Pair<String, String>
): HttpResponse<String> {
    val newHeaders = arrayOf(Pair("Content-Type", "application/json"), *headers)
    val requestBuilder = provideHttpRequestBuilder(uri, *newHeaders)
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))

    return HttpClient.newHttpClient()
        .sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).await()
}

suspend fun executePUTRequest(
    uri: URI,
    requestBody: String,
    vararg headers: Pair<String, String>
): HttpResponse<String> {
    val newHeaders = arrayOf(Pair("Content-Type", "application/json"), *headers)
    val requestBuilder = provideHttpRequestBuilder(uri, *newHeaders)
        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))

    return HttpClient.newHttpClient()
        .sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).await()
}

suspend fun executeDELETERequest(uri: URI, vararg headers: Pair<String, String>): HttpResponse<String> {
    val requestBuilder = provideHttpRequestBuilder(uri, *headers)
        .DELETE()

    return HttpClient.newHttpClient()
        .sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).await()
}