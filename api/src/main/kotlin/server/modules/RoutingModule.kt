package server.modules

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import usecases.recipe.*
import usecases.recipetype.*
import usecases.role.FindRole
import usecases.user.*
import usecases.userroles.AddRoleToUser
import web.recipe.*
import web.recipetype.*
import web.user.*

fun Application.routingModule() {
    routing {
        healthCheckRoute()
        recipeTypeRoutes()
        recipeRoutes()
        userRoutes()

        optionsRoutes()
    }
}

fun Routing.optionsRoutes() {
    //Recipe type
    options("recipetype") {
        call.response.header("Allow", "GET,POST,PUT")
        call.respond(HttpStatusCode.OK)
    }
    options("recipetype/{id}") {
        call.response.header("Allow", "GET,DELETE")
        call.respond(HttpStatusCode.OK)
    }

    //Recipe
    options("recipe") {
        call.response.header("Allow", "GET,POST,PUT")
        call.respond(HttpStatusCode.OK)
    }
    options("recipe/{id}") {
        call.response.header("Allow", "GET,DELETE")
        call.respond(HttpStatusCode.OK)
    }
    options("recipe/search") {
        call.response.header("Allow", "POST")
        call.respond(HttpStatusCode.OK)
    }

    //User
    options("user") {
        call.response.header("Allow", "POST,PUT")
        call.respond(HttpStatusCode.OK)
    }
    options("user/{id}") {
        call.response.header("Allow", "GET, DELETE")
        call.respond(HttpStatusCode.OK)
    }
    options("user/login") {
        call.response.header("Allow", "POST")
        call.respond(HttpStatusCode.OK)
    }
}

fun Routing.healthCheckRoute() {
    get("health-check") {
        call.respond(HttpStatusCode.OK, "I'm alive, thanks for asking 👋")
    }
}

fun Routing.recipeTypeRoutes() {
    route("recipetype") {
        get {
            val useCase by call.di().instance<GetAllRecipeTypes>()
            GetAllRecipeTypesHandler(useCase).handle(call)
        }
        get("{id}") {
            val useCase by call.di().instance<FindRecipeType>()
            FindRecipeTypeHandler(useCase).handle(call)
        }
        authenticate("admin") {
            post {
                val useCase by call.di().instance<CreateRecipeType>()
                CreateRecipeTypeHandler(useCase).handle(call)
            }
            put {
                val useCase by call.di().instance<UpdateRecipeType>()
                UpdateRecipeTypeHandler(useCase).handle(call)
            }
            delete("{id}") {
                val useCase by call.di().instance<DeleteRecipeType>()
                DeleteRecipeTypeHandler(useCase).handle(call)
            }
        }
    }
}

fun Routing.recipeRoutes() {
    route("recipe") {
        get {
            val useCase by call.di().instance<GetAllRecipes>()
            GetAllRecipesHandler(useCase).handle(call)
        }
        get("{id}") {
            val useCase by call.di().instance<FindRecipe>()
            FindRecipeHandler(useCase).handle(call)
        }
        post {
            val useCase by call.di().instance<CreateRecipe>()
            CreateRecipeHandler(useCase).handle(call)
        }
        put {
            val useCase by call.di().instance<UpdateRecipe>()
            UpdateRecipeHandler(useCase).handle(call)
        }
        delete("{id}") {
            val useCase by call.di().instance<DeleteRecipe>()
            DeleteRecipeHandler(useCase).handle(call)
        }
    }
}

fun Routing.userRoutes() {
    route("user") {
        post("login") {
            val useCase by call.di().instance<LoginUser>()
            LoginUserHandler(useCase).handle(call)
        }
        post {
            val createUserUseCase by call.di().instance<CreateUser>()
            val findRoleUseCase by call.di().instance<FindRole>()
            val addRoleToUserUseCase by call.di().instance<AddRoleToUser>()
            CreateUserHandler(
                createUser = createUserUseCase,
                findRole = findRoleUseCase,
                addRoleToUser = addRoleToUserUseCase
            ).handle(call)
        }
        authenticate("user") {
            get("{id}") {
                val useCase by call.di().instance<FindUser>()
                FindUserHandler(useCase).handle(call)
            }
        }
        authenticate("admin") {
            put {
                val useCase by call.di().instance<UpdateUser>()
                UpdateUserHandler(useCase).handle(call)
            }
            delete("{id}") {
                val useCase by call.di().instance<DeleteUser>()
                DeleteUserHandler(useCase).handle(call)
            }
        }
    }
}