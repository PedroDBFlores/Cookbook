package web.recipephoto

import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import server.modules.contentNegotiationModule
import usecases.recipephoto.CreateRecipePhoto

internal class CreateRecipePhotoHandlerTest : DescribeSpec({
    fun createTestServer(id: Int, createRecipePhoto: CreateRecipePhoto): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/api/recipe/$id/photo") { CreateRecipePhotoHandler(createRecipePhoto).handle(call) }
        }
    }

    describe("Create recipe photo handler") {
//        it("receives a photo") {
//            val recipePhoto = recipePhotoGenerator.next()
//            val expectedParameters = CreateRecipePhoto.Parameters(
//                recipePhoto = recipePhoto
//            )
//            val expectedId = Arb.int(1..100).next()
//            val createRecipePhotoMock = mockk<CreateRecipePhoto> {
//                every { this@mockk(expectedParameters) } returns expectedId
//            }
//
//            withTestApplication(
//                moduleFunction = createTestServer(recipePhoto.recipeId, createRecipePhotoMock)
//            ) {
//                with(
//                    handleRequest(HttpMethod.Post, "/api/recipe/${recipePhoto.recipeId}/photo") {
//                        setBody()
//                        addHeader("Content-Type", "multipart/form-data")
//                    }
//                ) {
//
//                }
//            }
//        }
    }
})
