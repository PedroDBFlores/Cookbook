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
import usecases.role.*
import usecases.user.*
import usecases.userroles.AddRoleToUser
import usecases.userroles.DeleteRoleFromUser
import usecases.userroles.GetUserRoles
import web.recipe.*
import web.recipetype.*
import web.roles.*
import web.user.*
import web.userroles.AddRoleToUserHandler
import web.userroles.DeleteRoleFromUserHandler
import web.userroles.GetUserRolesHandler

fun Application.routingModule() {
    routing {
        healthCheckRoute()
        recipeTypeRoutes()
        recipeRoutes()
        userRoutes()
        roleRoutes()
        userRolesRoutes()

        optionsRoutes()
    }
}

fun Routing.optionsRoutes() {
    suspend fun ApplicationCall.handleOptionsHeaders(allowedMethods: String) {
        response.header("Access-Control-Allow-Methods", allowedMethods)
        respond(HttpStatusCode.OK)
    }

    // Recipe type
    options("recipetype") {
        call.handleOptionsHeaders("GET,POST,PUT")
    }
    options("recipetype/{id}") {
        call.handleOptionsHeaders("GET,DELETE")
    }

    // Recipe
    options("recipe") {
        call.handleOptionsHeaders("GET,POST,PUT")
    }
    options("recipe/{id}") {
        call.handleOptionsHeaders("GET,DELETE")
    }
    options("recipe/search") {
        call.handleOptionsHeaders("POST")
    }

    // User
    options("user") {
        call.handleOptionsHeaders("POST,PUT")
    }
    options("user/{id}") {
        call.handleOptionsHeaders("GET,DELETE")
    }
    options("user/login") {
        call.handleOptionsHeaders("POST")
    }

    // Role
    options("role") {
        call.handleOptionsHeaders("GET,POST,PUT")
    }
    options("role/{id}") {
        call.handleOptionsHeaders("GET,DELETE")
    }

    // User roles
    options("userroles") {
        call.handleOptionsHeaders("POST")
    }
    options("userroles/{userId}") {
        call.handleOptionsHeaders("GET")
    }
    options("userroles/{userId}/{roleId}") {
        call.handleOptionsHeaders("DELETE")
    }
}

fun Routing.healthCheckRoute() {
    get("health-check") {
        call.respond(HttpStatusCode.OK, "I'm alive, thanks for asking 👋")
    }
}

fun Routing.recipeTypeRoutes() {
    route("recipetype") {
        authenticate("user") {
            get {
                val useCase by call.di().instance<GetAllRecipeTypes>()
                GetAllRecipeTypesHandler(useCase).handle(call)
            }
            get("{id}") {
                val useCase by call.di().instance<FindRecipeType>()
                FindRecipeTypeHandler(useCase).handle(call)
            }
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
        post("search") {
            val useCase by call.di().instance<SearchRecipe>()
            SearchRecipeHandler(useCase).handle(call)
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

fun Routing.roleRoutes() {
    route("role") {
        authenticate("admin") {
            get {
                val useCase by call.di().instance<GetAllRoles>()
                GetAllRolesHandler(useCase).handle(call)
            }
            get("{id}") {
                val useCase by call.di().instance<FindRole>()
                FindRoleHandler(useCase).handle(call)
            }
            post {
                val useCase by call.di().instance<CreateRole>()
                CreateRoleHandler(useCase).handle(call)
            }
            put {
                val useCase by call.di().instance<UpdateRole>()
                UpdateRoleHandler(useCase).handle(call)
            }
            delete("{id}") {
                val useCase by call.di().instance<DeleteRole>()
                DeleteRoleHandler(useCase).handle(call)
            }
        }
    }
}

fun Routing.userRolesRoutes() {
    route("userroutes") {
        authenticate("admin") {
            get("{userId}") {
                val useCase by call.di().instance<GetUserRoles>()
                GetUserRolesHandler(useCase).handle(call)
            }
            post {
                val useCase by call.di().instance<AddRoleToUser>()
                AddRoleToUserHandler(useCase).handle(call)
            }
            delete("{userId}/{roleId}") {
                val useCase by call.di().instance<DeleteRoleFromUser>()
                DeleteRoleFromUserHandler(useCase).handle(call)
            }
        }
    }
}
