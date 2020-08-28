package web.user

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import model.CreateResult
import model.Role
import model.User
import server.modules.contentNegotiationModule
import usecases.role.FindRole
import usecases.user.CreateUser
import usecases.userroles.AddRoleToUser
import utils.JsonHelpers.createJSONObject
import utils.JsonHelpers.toJson

internal class CreateUserHandlerTest : DescribeSpec({
    val basicRole = Role(id = 1, name = "User", code = "USER")
    val basicUser = User(id = 123, name = "name", userName = "username")

    val findRole = mockk<FindRole> {
        every { this@mockk(FindRole.Parameters("USER")) } returns basicRole
    }
    val addRoleToUser = mockk<AddRoleToUser> {
        every { this@mockk(AddRoleToUser.Parameters(basicUser.id, basicRole.id)) } just Runs
    }

    fun createTestServer(createUser: CreateUser): Application.() -> Unit = {
        contentNegotiationModule()
        routing {
            post("/user") { CreateUserHandler(createUser, findRole, addRoleToUser).handle(call) }
        }
    }

    describe("Create user handler") {
        it("returns 201 on a successful creation") {
            val jsonBody = createJSONObject(
                "name" to basicUser.name,
                "userName" to basicUser.userName,
                "password" to "password"
            )

            val createUser = mockk<CreateUser> {
                every {
                    this@mockk(
                        CreateUser.Parameters(
                            basicUser.copy(id = 0, passwordHash = ""),
                            "password"
                        )
                    )
                } returns basicUser.id
            }

            withTestApplication(moduleFunction = createTestServer(createUser)) {
                with(handleRequest(HttpMethod.Post, "/user") {
                    setBody(jsonBody)
                    addHeader("Content-Type", "application/json")
                }) {
                    response.status().shouldBe(HttpStatusCode.Created)
                    response.content.shouldMatchJson(CreateResult(basicUser.id).toJson())
                    verify(exactly = 1) {
                        createUser(CreateUser.Parameters(basicUser.copy(id = 0, passwordHash = ""), "password"))
                        findRole(FindRole.Parameters("USER"))
                        addRoleToUser(AddRoleToUser.Parameters(basicUser.id, basicRole.id))
                    }
                }
            }
        }

        val createUserRepresenterMap = mapOf<String, Any>(
            "name" to "name",
            "userName" to "username",
            "password" to "password"
        )

        arrayOf(
            row(createJSONObject("non" to "conformant"), "the provided body doesn't match the required JSON"),
            row((createUserRepresenterMap + mapOf<String, Any>("name" to "")).toJson(), "the name field is invalid"),
            row(
                (createUserRepresenterMap + mapOf<String, Any>("userName" to "")).toJson(),
                "the userName field is invalid"
            ),
            row(
                (createUserRepresenterMap + mapOf<String, Any>("password" to "")).toJson(),
                "the password field is invalid"
            )
        ).forEach { (jsonBody, description) ->
            it("returns 400 when $description") {
                val createUser = mockk<CreateUser>()

                withTestApplication(moduleFunction = createTestServer(createUser)) {
                    with(handleRequest(HttpMethod.Post, "/user") {
                        setBody(jsonBody)
                        addHeader("Content-Type", "application/json")
                    }) {
                        response.status().shouldBe(HttpStatusCode.BadRequest)
                        verify { createUser wasNot called }
                    }
                }
            }
        }
    }
})