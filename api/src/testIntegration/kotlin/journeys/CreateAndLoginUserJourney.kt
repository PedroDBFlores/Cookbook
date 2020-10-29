package journeys

import actions.UserActions
import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.eclipse.jetty.http.HttpStatus
import server.CookbookApi
import utils.DatabaseMigration
import utils.JsonHelpers

class CreateAndLoginUserJourney : BehaviorSpec({
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/application.conf")
    val baseUrl = "http://localhost:${configuration.api.port}"
    lateinit var cookbookAPI: CookbookApi

    beforeSpec {
        DatabaseMigration.migrateDB(configuration)
        cookbookAPI = CookbookApi(configuration)
        cookbookAPI.start()
    }

    afterSpec {
        cookbookAPI.close()
    }

    Given("No user") {
        `when`("I create a user on the API") {
            then("I have success") {
                val createUserRequestBody = JsonHelpers.createJSONObject(
                    "name" to "name",
                    "userName" to "username",
                    "password" to "password"
                )

                val createUserResponse = UserActions.createUser(baseUrl, createUserRequestBody)

                createUserResponse.statusCode().shouldBe(HttpStatus.CREATED_201)
                createUserResponse.body().shouldContainJsonKey("id")
            }

            then("I'm able to login") {
                val loginUserRequestBody = JsonHelpers.createJSONObject(
                    "userName" to "username",
                    "password" to "password"
                )

                val loginUserResponse = UserActions.loginUser(baseUrl, loginUserRequestBody)

                loginUserResponse.statusCode().shouldBe(HttpStatus.OK_200)
                loginUserResponse.body().shouldContainJsonKey("token")
            }
        }

        `when`("I try to login with a non-existing user") {
            then("I am forbidden") {
                val loginUserRequestBody = JsonHelpers.createJSONObject(
                    "userName" to "non",
                    "password" to "existing"
                )

                val loginUserResponse = UserActions.loginUser(baseUrl, loginUserRequestBody)

                with(loginUserResponse) {
                    statusCode().shouldBe(HttpStatus.FORBIDDEN_403)
                }
            }
        }
    }
})
