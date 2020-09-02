package server.modules

import io.kotest.assertions.ktor.shouldHaveHeader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import org.h2.command.ddl.CreateUser
import org.kodein.di.bind
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import usecases.recipe.*
import usecases.recipetype.*
import usecases.user.DeleteUser
import usecases.user.FindUser
import usecases.user.LoginUser
import usecases.user.UpdateUser

class RoutingModuleTest : DescribeSpec({

    fun Application.injectTestDependencies() {
        di {
            //Recipe type
            bind<FindRecipeType>() with singleton { mockk(relaxed = true) }
            bind<GetAllRecipeTypes>() with singleton { mockk(relaxed = true) }
            bind<CreateRecipeType>() with singleton { mockk(relaxed = true) }
            bind<UpdateRecipeType>() with singleton { mockk(relaxed = true) }
            bind<DeleteRecipeType>() with singleton { mockk(relaxed = true) }
            //Recipe
            bind<FindRecipe>() with singleton { mockk(relaxed = true) }
            bind<GetAllRecipes>() with singleton { mockk(relaxed = true) }
            bind<CreateRecipe>() with singleton { mockk(relaxed = true) }
            bind<UpdateRecipe>() with singleton { mockk(relaxed = true) }
            bind<DeleteRecipe>() with singleton { mockk(relaxed = true) }
            // User
            bind<CreateUser>() with singleton { mockk(relaxed = true) }
            bind<LoginUser>() with singleton { mockk(relaxed = true) }
            bind<FindUser>() with singleton { mockk(relaxed = true) }
            bind<UpdateUser>() with singleton { mockk(relaxed = true) }
            bind<DeleteUser>() with singleton { mockk(relaxed = true) }
        }
    }

    fun createTestServer(): Application.() -> Unit = {
        contentNegotiationModule()
        injectTestDependencies()
        install(Authentication) {
            basic("user") {}
            basic("admin") {}
        }
        routingModule()
    }

    describe("Routing module test") {
        it("checks that the health check endpoint is mapped") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Get, "/health-check")) {
                    with(response) {
                        status().shouldBe(HttpStatusCode.OK)
                        content.shouldBe("I'm alive, thanks for asking 👋")
                    }
                }
            }
        }

        it("checks that the routes for the recipe types are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/recipetype")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,POST,PUT")
                }
                with(handleRequest(HttpMethod.Options, "/recipetype/123")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
                }
            }
        }

        it("checks that the routes for the recipe are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/recipe")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,POST,PUT")
                }
                with(handleRequest(HttpMethod.Options, "/recipe/123")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
                }
                with(handleRequest(HttpMethod.Options, "/recipe/search")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "POST")
                }
            }
        }

        it("checks that the routes for the user are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/user")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "POST,PUT")
                }
                with(handleRequest(HttpMethod.Options, "/user/123")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
                }
                with(handleRequest(HttpMethod.Options, "/user/login")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "POST")
                }
            }
        }

        it("checks that the routes for the roles are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/role")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,POST,PUT")

                }
                with(handleRequest(HttpMethod.Options, "/role/123")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET,DELETE")
                }
            }
        }

        it("checks that the routes for the user roles are mapped with OPTIONS handler") {
            withTestApplication(moduleFunction = createTestServer()) {
                with(handleRequest(HttpMethod.Options, "/userroles")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "POST")
                }
                with(handleRequest(HttpMethod.Options, "/userroles/123")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "GET")
                }
                with(handleRequest(HttpMethod.Options, "/userroles/123/456")) {
                    response.shouldHaveHeader("Access-Control-Allow-Methods", "DELETE")
                }
            }
        }
    }
})