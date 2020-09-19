package adapters.authentication

import com.auth0.jwt.exceptions.JWTVerificationException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.maps.shouldContainKeys
import io.kotest.matchers.shouldBe
import model.User

internal class JWTManagerImplTest : DescribeSpec({
    describe("JWTManager test") {
        val basicUser = User(id = 123, name = "Marco Paulo", userName = "marco_paulo")

        val basicJwtManager = JWTManagerImpl(
            domain = "http://my-domain",
            audience = "my-audience",
            realm = "my-realm",
            allowedRoles = listOf(ApplicationRoles.USER),
            algorithmSecret = "secret"
        )

        it("generates a token for a User with the required JWT parameters") {
            val user = basicUser.copy(roles = listOf("USER"))

            val token = basicJwtManager.generateToken(user)

            with(basicJwtManager.decodeToken(token)) {
                print(this.claims)
                issuer.shouldBe("http://my-domain")
                audience.shouldContain("my-audience")
                subject.shouldBe(user.id.toString())
                claims.shouldContainKeys("userName", "name", "roles", "iss")
                claims.getValue("userName").asString().shouldBe(user.userName)
                claims.getValue("name").asString().shouldBe(user.name)
                claims.getValue("roles").asArray(String::class.java).shouldBe(user.roles)
            }
        }

        it("should verify a token that is generated by this JWT configuration") {
            val user = basicUser.copy(roles = listOf(ApplicationRoles.USER.name))
            val token = basicJwtManager.generateToken(user)

            val act = { basicJwtManager.verifier.verify(token) }

            shouldNotThrow<JWTVerificationException> (act)
        }

        arrayOf(
            row(
                JWTManagerImpl(
                    domain = "http://not-my-domain",
                    audience = "my-audience",
                    realm = "my-realm",
                    allowedRoles = listOf(ApplicationRoles.USER),
                    algorithmSecret = "secret"
                ).generateToken(basicUser), "a token is not from the same domain"
            ),
            row(
                JWTManagerImpl(
                    domain = "http://my-domain",
                    audience = "not-my-audience",
                    realm = "my-realm",
                    allowedRoles = listOf(ApplicationRoles.USER),
                    algorithmSecret = "secret"
                ).generateToken(basicUser), "a token is not from the same audience"
            ),
            row(
                basicJwtManager.generateToken(basicUser.copy(roles = listOf("UNKNOWN"))),
                "a token has no allowed role for this configuration"
            )
        ).forEach { (token, desc) ->
            it("should fail on token verification when $desc") {
                val act = { basicJwtManager.verifier.verify(token) }

                shouldThrow<JWTVerificationException> (act)
            }
        }
    }
})