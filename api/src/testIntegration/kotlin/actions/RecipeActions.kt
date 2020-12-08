package actions

import java.net.URI
import java.net.http.HttpResponse

object RecipeActions {
    suspend fun searchRecipe(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/recipe/search"),
            requestBody = requestBody,
        )

    suspend fun getRecipeType(baseUrl: String, id: Int) = executeGETRequest(
        uri = URI("$baseUrl/recipe/$id")
    )

    suspend fun createRecipe(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/recipe"),
            requestBody = requestBody
        )

    suspend fun updateRecipe(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePUTRequest(
            uri = URI("$baseUrl/recipe"),
            requestBody = requestBody
        )

    suspend fun deleteRecipeType(baseUrl: String, id: Int) = executeDELETERequest(
        uri = URI("$baseUrl/recipe/$id")
    )
}
