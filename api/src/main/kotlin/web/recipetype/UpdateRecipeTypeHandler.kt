package web.recipetype

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import model.RecipeType
import ports.KtorHandler
import usecases.recipetype.UpdateRecipeType
import server.extensions.receiveOrThrow

class UpdateRecipeTypeHandler(private val updateRecipeType: UpdateRecipeType) : KtorHandler {

    override suspend fun handle(call: ApplicationCall) {
        val recipeType = call.receiveOrThrow<UpdateRecipeTypeRepresenter>()
            .toRecipeType()
        updateRecipeType(UpdateRecipeType.Parameters(recipeType))
        call.respond(HttpStatusCode.OK)
    }

    data class UpdateRecipeTypeRepresenter(val id: Int, val name: String) {
        init {
            check(id > 0) { "Field 'id' must be bigger than zero" }
            check(name.isNotEmpty()) { "Field 'name' must not be empty" }
        }

        fun toRecipeType() = RecipeType(id = id, name = name)
    }


}
