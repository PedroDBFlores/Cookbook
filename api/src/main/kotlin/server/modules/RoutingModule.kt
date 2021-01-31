package server.modules

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import usecases.recipe.*
import usecases.recipetype.*
import web.recipe.*
import web.recipetype.*

fun Application.routingModule() {
    routing {
        staticRoute()
        healthCheckRoute()
        recipeTypeRoutes()
        recipeRoutes()
        optionsRoutes()
    }
}

fun Routing.staticRoute() {
    static("/") {
        resources(resourcePackage = "static")
    }
    defaultResource(resource = "index.html", resourcePackage = "static")
}

fun Routing.optionsRoutes() {
    suspend fun ApplicationCall.handleOptionsHeaders(allowedMethods: String) {
        response.header("Access-Control-Allow-Methods", allowedMethods)
        respond(HttpStatusCode.OK)
    }

    // Root route
    options("/") {
        call.handleOptionsHeaders("GET")
    }

    // Recipe type
    options("/api/recipetype") {
        call.handleOptionsHeaders("GET,POST,PUT")
    }
    options("/api/recipetype/{id}") {
        call.handleOptionsHeaders("GET,DELETE")
    }

    // Recipe
    options("/api/recipe") {
        call.handleOptionsHeaders("GET,POST,PUT")
    }
    options("/api/recipe/{id}") {
        call.handleOptionsHeaders("GET,DELETE")
    }
    options("/api/recipe/search") {
        call.handleOptionsHeaders("POST")
    }
}

fun Routing.healthCheckRoute() {
    get("/health-check") {
        call.respond(HttpStatusCode.OK, "I'm alive, thanks for asking 👋")
    }
}

fun Routing.recipeTypeRoutes() {
    route("/api/recipetype") {
        get {
            val useCase by call.di().instance<GetAllRecipeTypes>()
            GetAllRecipeTypesHandler(useCase).handle(call)
        }
        get("{id}") {
            val useCase by call.di().instance<FindRecipeType>()
            FindRecipeTypeHandler(useCase).handle(call)
        }
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

fun Routing.recipeRoutes() {
    route("/api/recipe") {
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
