package web.recipephoto

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.CreateResult
import ports.ImageChecker
import ports.ImageState
import server.modules.contentNegotiationModule
import server.modules.exceptionInterceptorModule
import usecases.recipephoto.CreateRecipePhoto
import utils.JsonHelpers.toJson

internal class CreateRecipePhotoHandlerTest : DescribeSpec({
    fun createTestServer(
        createRecipePhoto: CreateRecipePhoto,
        imageChecker: ImageChecker
    ): Application.() -> Unit = {
        exceptionInterceptorModule()
        contentNegotiationModule()
        routing {
            post("/api/recipe/{id}/photo") {
                CreateRecipePhotoHandler(
                    createRecipePhoto = createRecipePhoto,
                    imageChecker = imageChecker
                ).handle(call)
            }
        }
    }

    describe("Create recipe photo handler") {
        it("receives a valid photo") {
            val imageStreamSource = { this.javaClass.getResourceAsStream("/resize_test.png")!! }
            val expectedRecipeId = Arb.int(1..100).next()
            val expectedId = Arb.int(1..100).next()
            val validImage = ImageState.Valid(imageStreamSource())
            val imageChecker = mockk<ImageChecker> {
                every { check(any()) } returns validImage
            }
            val createRecipePhotoMock = mockk<CreateRecipePhoto> {
                every { this@mockk(ofType()) } returns expectedId
            }

            withTestApplication(createTestServer(createRecipePhotoMock, imageChecker)) {
                with(
                    handleRequest(HttpMethod.Post, "/api/recipe/$expectedRecipeId/photo?name=Main") {
                        setBody(imageStreamSource().readAllBytes())
                        addHeader("Content-Type", "image/png")
                    }
                ) {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson(CreateResult(expectedId).toJson())
                    verify(exactly = 1) {
                        imageChecker.check(ofType())
                        createRecipePhotoMock(
                            CreateRecipePhoto.Parameters(
                                name = "Main",
                                recipeId = expectedRecipeId,
                                validImage = validImage,
                            )
                        )
                    }
                }
            }
        }

        arrayOf(
            row(byteArrayOf(1, 2, 3), "", "name query parameter is missing"),
            row(byteArrayOf(1, 2, 3), "name=", "no name is provided"),
        ).forEach { (image, imageNameParameter, testDescription) ->
            it("returns 400 when $testDescription") {
                val imageCheckerMock = mockk<ImageChecker>()
                val createRecipePhotoMock = mockk<CreateRecipePhoto>()

                withTestApplication(createTestServer(createRecipePhotoMock, imageCheckerMock)) {
                    with(
                        handleRequest(HttpMethod.Post, "/api/recipe/123/photo?$imageNameParameter") {
                            setBody(image)
                            addHeader("Content-Type", "image/png")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify {
                            imageCheckerMock wasNot called
                            createRecipePhotoMock wasNot called
                        }
                    }
                }
            }
        }

        it("returns 415 if provided photo is not valid") {
            val imageChecker = mockk<ImageChecker> {
                every { check(any()) } returns ImageState.NotAnImage
            }
            val createRecipePhotoMock = mockk<CreateRecipePhoto>()

            withTestApplication(createTestServer(createRecipePhotoMock, imageChecker)) {
                with(
                    handleRequest(HttpMethod.Post, "/api/recipe/123/photo?name=Main") {
                        setBody(byteArrayOf(1, 2, 3))
                        addHeader("Content-Type", "image/png")
                    }
                ) {
                    response.status().shouldBe(HttpStatusCode.UnsupportedMediaType)
                    verify(exactly = 1) {
                        imageChecker.check(ofType())
                    }
                    verify {
                        createRecipePhotoMock wasNot called
                    }
                }
            }
        }
    }
})
