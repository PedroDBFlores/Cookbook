package web.user

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import server.modules.contentNegotiationModule
import usecases.user.UpdateUser
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class UpdateUserHandlerTest : DescribeSpec({
    fun createTestServer(updateUser: UpdateUser): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            put("/user") { UpdateUserHandler(updateUser).handle(call) }
        }
    }

    describe("Update user handler") {
        val updateUserRequestBody = mapOf<String, Any?>(
            "id" to 1,
            "name" to "newName",
            "oldPassword" to "old",
            "newPassword" to "new"
        )

        it("updates an user's name only") {
            val updateUser = mockk<UpdateUser> {
                every { this@mockk(UpdateUser.Parameters(7, "newName")) } just Runs
            }

            withTestApplication(moduleFunction = createTestServer(updateUser)) {
                with(
                    handleRequest(HttpMethod.Put, "/user") {
                        setBody(
                            createJSONObject(
                                "id" to 7,
                                "name" to "newName",
                                "oldPassword" to null,
                                "newPassword" to null
                            )
                        )
                        addHeader("Content-Type", "application/json")
                    }
                ) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    verify(exactly = 1) { updateUser(UpdateUser.Parameters(7, "newName")) }
                }
            }
        }

        it("updates an user's name and passwords") {
            val updateUser = mockk<UpdateUser> {
                every { this@mockk(UpdateUser.Parameters(1, "newName", "old", "new")) } just Runs
            }

            withTestApplication(moduleFunction = createTestServer(updateUser)) {
                with(
                    handleRequest(HttpMethod.Put, "/user") {
                        setBody(updateUserRequestBody.toJson())
                        addHeader("Content-Type", "application/json")
                    }
                ) {
                    response.status().shouldBe(HttpStatusCode.OK)
                    verify(exactly = 1) { updateUser(UpdateUser.Parameters(1, "newName", "old", "new")) }
                }
            }
        }

        arrayOf(
            row(createJSONObject("non" to "conformant"), "a wrong JSON is provided"),
            row((updateUserRequestBody + mapOf<String, Any?>("id" to null)).toJson(), "the user id is null"),
            row((updateUserRequestBody + mapOf<String, Any?>("id" to 0)).toJson(), "the user id is not valid"),
            row((updateUserRequestBody + mapOf<String, Any?>("name" to null)).toJson(), "the name is null"),
            row(
                (updateUserRequestBody + mapOf<String, Any?>("oldPassword" to null)).toJson(),
                "the old password isn't provided with the new one"
            ),
        ).forEach { (jsonBody, description) ->
            it("returns 400 when $description") {
                val updateUser = mockk<UpdateUser>()
                println(jsonBody)
                withTestApplication(moduleFunction = createTestServer(updateUser)) {
                    with(
                        handleRequest(HttpMethod.Put, "/user") {
                            setBody(jsonBody)
                            addHeader("Content-Type", "application/json")
                        }
                    ) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { updateUser wasNot Called }
                    }
                }
            }
        }
    }
})
