package journeys

import actions.RecipeActions
import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import actions.UserActions
import io.kotest.core.spec.style.BehaviorSpec
import server.CookbookApi
import utils.DatabaseMigration
import utils.JsonHelpers
import utils.JsonHelpers.getJsonValue

class RecipeJourney: BehaviorSpec({
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/application.conf")
    val baseUrl = "http://localhost:${configuration.api.port}"
    lateinit var cookbookAPI: CookbookApi
    lateinit var userJWTToken: String

    val userLoginRequestBody = JsonHelpers.createJSONObject(
        "userName" to "basicUser",
        "password" to "basicPassword"
    )

    beforeSpec{
        DatabaseMigration.migrateDB(configuration)
        cookbookAPI = CookbookApi(configuration)
        cookbookAPI.start()
        UserActions.createUser(
            baseUrl, JsonHelpers.createJSONObject(
                "name" to "basicName",
                "userName" to "basicUser",
                "password" to "basicPassword"
            )
        )
        userJWTToken = UserActions.loginUser(baseUrl, userLoginRequestBody).body().getJsonValue("token")
    }

    afterSpec{
        cookbookAPI.close()
    }

    Given("I want to search for a recipe"){
        `when`("I have a valid JWT token"){
            then("I'm able to search"){
                val searchRecipes = RecipeActions.searchRecipe(
                    baseUrl = baseUrl,
                    requestBody = "",
                    jwtToken = userJWTToken
                )
            }
        }
    }
})