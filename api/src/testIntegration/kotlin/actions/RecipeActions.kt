package actions

import java.net.URI
import java.net.http.HttpResponse

object RecipeActions {
    suspend fun searchRecipe(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/api/recipe/search"),
            requestBody = requestBody,
        )

    suspend fun getRecipe(baseUrl: String, id: Int) = executeGETRequest(
        uri = URI("$baseUrl/api/recipe/$id")
    )

    suspend fun createRecipe(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/api/recipe"),
            requestBody = requestBody
        )

    suspend fun updateRecipe(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePUTRequest(
            uri = URI("$baseUrl/api/recipe"),
            requestBody = requestBody
        )

    suspend fun deleteRecipe(baseUrl: String, id: Int) = executeDELETERequest(
        uri = URI("$baseUrl/api/recipe/$id")
    )
}
