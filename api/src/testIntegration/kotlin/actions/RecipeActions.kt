package actions

import java.net.URI
import java.net.http.HttpResponse

object RecipeActions {
    suspend fun searchRecipe(baseUrl: String, requestBody: String, jwtToken: String? = null): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/recipe/search"),
            requestBody = requestBody,
            headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
        )

    suspend fun getRecipeType(baseUrl: String, id: Int, jwtToken: String? = null) = executeGETRequest(
        uri = URI("$baseUrl/recipe/$id"),
        headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
    )

    suspend fun createRecipe(baseUrl: String, requestBody: String, jwtToken: String? = null): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/recipe"),
            requestBody = requestBody,
            headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
        )

    suspend fun updateRecipe(baseUrl: String, requestBody: String, jwtToken: String? = null): HttpResponse<String> =
        executePUTRequest(
            uri = URI("$baseUrl/recipe"),
            requestBody = requestBody,
            headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
        )

    suspend fun deleteRecipeType(baseUrl: String, id: Int, jwtToken: String? = null) = executeDELETERequest(
        uri = URI("$baseUrl/recipe/$id"),
        headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
    )
}