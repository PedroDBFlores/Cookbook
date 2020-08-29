package journey

import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import org.eclipse.jetty.http.HttpStatus
import server.CookbookApi
import utils.DatabaseMigration
import utils.JsonHelpers
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers.ofString

class CreateAndLoginUserIT : BehaviorSpec({
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")
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
                val createUserRequest = HttpRequest
                    .newBuilder()
                    .uri(URI("$baseUrl/user"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(createUserRequestBody))
                    .build()

                val createUserResponse = newHttpClient().sendAsync(createUserRequest, ofString()).await()

                createUserResponse.statusCode().shouldBe(HttpStatus.CREATED_201)
                createUserResponse.body().shouldContainJsonKey("id")
            }

            then("I'm able to login") {
                val loginUserRequestBody = JsonHelpers.createJSONObject(
                    "userName" to "username",
                    "password" to "password"
                )
                val loginUserRequest = HttpRequest
                    .newBuilder()
                    .uri(URI("$baseUrl/user/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginUserRequestBody))
                    .build()

                val loginUserResponse = newHttpClient().sendAsync(loginUserRequest, ofString()).await()

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
                val loginUserRequest = HttpRequest
                    .newBuilder()
                    .uri(URI("$baseUrl/user/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginUserRequestBody))
                    .build()

                val loginUserResponse = newHttpClient().sendAsync(loginUserRequest, ofString()).await()

                with(loginUserResponse) {
                    statusCode().shouldBe(HttpStatus.FORBIDDEN_403)
                }
            }
        }
    }

})