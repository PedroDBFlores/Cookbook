package web.recipe

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import ports.KtorHandler
import server.extensions.receiveOrThrow
import usecases.recipe.SearchRecipe

class SearchRecipeHandler(private val searchRecipe: SearchRecipe) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val parameters = call.receiveOrThrow<SearchRecipeRepresenter>()
            .toParameters()
        call.respond(HttpStatusCode.OK, searchRecipe(parameters))
    }

    @Serializable
    private data class SearchRecipeRepresenter(
        val name: String? = null,
        val description: String? = null,
        val recipeTypeId: Int? = null,
        val pageNumber: Int = 1,
        val itemsPerPage: Int = 10
    ) {
        init {
            recipeTypeId?.let { check(it > 0) { "Field 'recipeTypeId' must be bigger than 0" } }
            check(pageNumber > -1) { "Field 'pageNumber' must be 0 or bigger" }
            check(itemsPerPage > 0) { "Field 'itemsPerPage' must be bigger than 0" }
        }

        fun toParameters() = SearchRecipe.Parameters(
            name = name,
            description = description,
            recipeTypeId = recipeTypeId,
            pageNumber = pageNumber,
            itemsPerPage = itemsPerPage
        )
    }
}
