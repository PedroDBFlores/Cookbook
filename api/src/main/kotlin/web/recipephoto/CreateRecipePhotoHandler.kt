package web.recipephoto

import io.ktor.application.*
import ports.KtorHandler
import usecases.recipephoto.CreateRecipePhoto

class CreateRecipePhotoHandler(private val createRecipePhoto: CreateRecipePhoto) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        TODO("Not yet implemented")
    }
}
