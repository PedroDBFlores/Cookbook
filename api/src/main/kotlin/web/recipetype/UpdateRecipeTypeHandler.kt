package web.recipetype

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.RecipeType
import ports.KtorHandler
import usecases.recipetype.UpdateRecipeType
import server.extensions.validateReceivedBody

class UpdateRecipeTypeHandler(private val updateRecipeType: UpdateRecipeType) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipeType = call.validateReceivedBody<UpdateRecipeTypeRepresenter> {
            check(it.id > 0) { "Field 'id' must be bigger than 0" }
        }
            .toRecipeType()
        updateRecipeType(recipeType)
        call.respond(HttpStatusCode.OK)
    }

    data class UpdateRecipeTypeRepresenter(val id: Int, val name: String) {
        fun toRecipeType() = RecipeType(id = id, name = name)
    }


}
