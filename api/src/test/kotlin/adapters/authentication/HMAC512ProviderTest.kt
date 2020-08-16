package adapters.authentication

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotHaveLength
import utils.DTOGenerator

internal class HMAC512ProviderTest : DescribeSpec({
    describe("HMAC512 provider") {

        arrayOf(
            row(DTOGenerator.generateUser(), emptyList(), "no roles"),
            row(
                DTOGenerator.generateUser(
                    roles = listOf(
                        "Role1", "Role2"
                    )
                ), listOf("Role1", "Role2"), "roles"
            )
        ).forEach { (user, expectedRoles, desc) ->
            it("has the required claims for the Cookbook application when the user has $desc") {
                val secret = "too-secret"
                val jwtProvider = HMAC512Provider.provide(secret)

                val jwtToken = jwtProvider.generateToken(user)
                val decodedJwt = jwtProvider.validateToken(jwtToken)

                jwtToken.shouldNotHaveLength(0)
                decodedJwt.shouldNotBeNull()
                with(decodedJwt) {
                    audience.shouldContain("cookbook")
                    subject.shouldBe(user.id.toString())
                    getClaim("username").asString().shouldBe(user.username)
                    getClaim("name").asString().shouldBe(user.name)
                    getClaim("roles").asArray(String::class.java).shouldBe(expectedRoles)
                }
            }
        }
    }
})