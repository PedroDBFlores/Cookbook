package web.recipephoto

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import model.CreateResult
import ports.ImageChecker
import ports.ImageState
import ports.KtorHandler
import usecases.recipephoto.CreateRecipePhoto

class CreateRecipePhotoHandler(
    private val createRecipePhoto: CreateRecipePhoto,
    private val imageChecker: ImageChecker
) : KtorHandler {
    override suspend fun handle(call: ApplicationCall) {
        val recipeId = call.parameters.getOrFail<Int>("id")

        val photoName = call.request.queryParameters.getOrFail("name")
        require(photoName.isNotEmpty()) { throw BadRequestException("Name cannot be empty") }

        val image = call.receiveStream()

        when (val result = imageChecker.check(image)) {
            is ImageState.NotAnImage -> call.respond(HttpStatusCode.UnsupportedMediaType)
            is ImageState.Valid -> {
                val id = createRecipePhoto(
                    toParameters(
                        recipeId = recipeId,
                        photoName = photoName,
                        validImage = result
                    )
                )
                call.respond(HttpStatusCode.Created, CreateResult(id))
            }
        }
    }

    private fun toParameters(recipeId: Int, photoName: String, validImage: ImageState.Valid) =
        CreateRecipePhoto.Parameters(
            recipeId = recipeId,
            name = photoName,
            validImage = validImage
        )
}
