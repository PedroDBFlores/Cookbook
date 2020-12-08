package actions

import java.net.URI
import java.net.http.HttpResponse

object RecipeTypeActions {
    suspend fun getRecipeType(baseUrl: String, id: Int) = executeGETRequest(
        uri = URI("$baseUrl/recipetype/$id")
    )

    suspend fun getRecipeTypes(baseUrl: String): HttpResponse<String> = executeGETRequest(
        uri = URI("$baseUrl/recipetype")
    )

    suspend fun createRecipeType(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePOSTRequest(
            uri = URI("$baseUrl/recipetype"),
            requestBody = requestBody
        )

    suspend fun updateRecipeType(baseUrl: String, requestBody: String): HttpResponse<String> =
        executePUTRequest(
            uri = URI("$baseUrl/recipetype"),
            requestBody = requestBody
        )

    suspend fun deleteRecipeType(baseUrl: String, id: Int) = executeDELETERequest(
        uri = URI("$baseUrl/recipetype/$id")
    )
}
