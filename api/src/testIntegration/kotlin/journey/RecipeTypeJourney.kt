package journey

import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import flows.RecipeTypeFlows
import flows.UserFlows
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.eclipse.jetty.http.HttpStatus
import server.CookbookApi
import utils.DatabaseMigration
import utils.JsonHelpers
import utils.JsonHelpers.getJsonValue

class RecipeTypeJourney : BehaviorSpec({
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")
    val baseUrl = "http://localhost:${configuration.api.port}"
    lateinit var cookbookAPI: CookbookApi

    val userLoginRequestBody = JsonHelpers.createJSONObject(
        "userName" to "basicUser",
        "password" to "basicPassword"
    )
    val adminLoginRequestBody = JsonHelpers.createJSONObject(
        "userName" to "itadmin",
        "password" to "integrationtest"
    )

    beforeSpec {
        DatabaseMigration.migrateDB(configuration)
        cookbookAPI = CookbookApi(configuration)
        cookbookAPI.start()
        UserFlows.createUser(
            baseUrl, JsonHelpers.createJSONObject(
                "name" to "basicName",
                "userName" to "basicUser",
                "password" to "basicPassword"
            )
        )
    }

    afterSpec {
        cookbookAPI.close()
    }

    Given("I want to get all the recipe types") {
        arrayOf(
            row(userLoginRequestBody, "user"),
            row(adminLoginRequestBody, "admin")
        ).forEach { (requestBody, userType) ->
            `when`("I have a JWT token with a $userType role") {
                then("I'm able to access the route and get them") {
                    val token = UserFlows.loginUser(baseUrl, requestBody).body().getJsonValue("token")

                    val getRecipeTypeResponse = RecipeTypeFlows.getRecipeTypes(baseUrl, token)

                    getRecipeTypeResponse.statusCode().shouldBe(HttpStatus.OK_200)
                }
            }
        }
        `when`("I haven't got a JWT token") {
            then("I'm unauthorized") {
                val getRecipeTypeResponse = RecipeTypeFlows.getRecipeTypes(baseUrl)

                getRecipeTypeResponse.statusCode().shouldBe(HttpStatus.UNAUTHORIZED_401)
            }
        }
    }

    Given("I want to create a new recipe type") {
        `when`("I try to create one") {
            and("I'm an admin"){
                then("I'm able to create it and check it's details"){

                }
            }
            and("I'm an user"){
                then("I'm unauthorized"){

                }
            }
        }
    }
})