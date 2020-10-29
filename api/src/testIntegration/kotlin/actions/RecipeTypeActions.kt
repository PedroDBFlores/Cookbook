package actions

import java.net.URI
import java.net.http.HttpResponse

object RecipeTypeActions {
    suspend fun getRecipeType(baseUrl: String, id: Int, jwtToken: String? = null) = executeGETRequest(
        uri = URI("$baseUrl/recipetype/$id"),
        headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
    )

    suspend fun getRecipeTypes(baseUrl: String, jwtToken: String? = null): HttpResponse<String> = executeGETRequest(
        uri = URI("$baseUrl/recipetype"),
        headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
    )

    suspend fun createRecipeType(baseUrl: String, requestBody: String, jwtToken: String? = null): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/recipetype"),
            requestBody = requestBody,
            headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
        )

    suspend fun updateRecipeType(baseUrl: String, requestBody: String, jwtToken: String? = null): HttpResponse<String> =
        executePUTRequest(
            uri = URI("$baseUrl/recipetype"),
            requestBody = requestBody,
            headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
        )

    suspend fun deleteRecipeType(baseUrl: String, id: Int, jwtToken: String? = null) = executeDELETERequest(
        uri = URI("$baseUrl/recipetype/$id"),
        headers = arrayOf(Pair("Authorization", "Bearer $jwtToken"))
    )
}
