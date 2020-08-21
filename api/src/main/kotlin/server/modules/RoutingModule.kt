package server.modules

import io.ktor.application.*
import io.ktor.http.*
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
        healthCheckRoute()
        recipeTypeRoutes()
        recipeRoutes()
    }
}

fun Routing.healthCheckRoute() {
    get("health-check") {
        call.respond(HttpStatusCode.OK, "I'm alive, thanks for asking 👋")
    }
}

fun Routing.recipeTypeRoutes() {
    route("/recipetype") {
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
    route("/recipe") {
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