package server.modules

import config.Dependencies
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import web.recipe.*
import web.recipetype.*

fun Application.routingModule() = routing {
    healthCheckRoute()
    recipeTypeRoutes()
    recipeRoutes()
    optionsRoutes()
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

fun Routing.healthCheckRoute() = get("/health-check") {
    call.respond(HttpStatusCode.OK, "I'm alive, thanks for asking 👋")
}

fun Routing.recipeTypeRoutes() = route("/api/recipetype") {
    with(Dependencies) {
        get {
            GetAllRecipeTypesHandler(getAllRecipeTypes = getAllRecipeTypes).handle(call)
        }
        get("{id}") {
            FindRecipeTypeHandler(findRecipeType = findRecipeType).handle(call)
        }
        post {
            CreateRecipeTypeHandler(createRecipeType = createRecipeType).handle(call)
        }
        put {
            UpdateRecipeTypeHandler(updateRecipeType = updateRecipeType).handle(call)
        }
        delete("{id}") {
            DeleteRecipeTypeHandler(deleteRecipeType = deleteRecipeType).handle(call)
        }
    }
}

fun Routing.recipeRoutes() = route("/api/recipe") {
    with(Dependencies) {
        get {
            GetAllRecipesHandler(getAllRecipes = getAllRecipes).handle(call)
        }
        get("{id}") {
            FindRecipeHandler(findRecipe = findRecipe).handle(call)
        }
        post("search") {
            SearchRecipeHandler(searchRecipe = searchRecipe).handle(call)
        }
        post {
            CreateRecipeHandler(createRecipe = createRecipe).handle(call)
        }
        put {
            UpdateRecipeHandler(updateRecipe = updateRecipe).handle(call)
        }
        delete("{id}") {
            DeleteRecipeHandler(deleteRecipe = deleteRecipe).handle(call)
        }
    }
}
