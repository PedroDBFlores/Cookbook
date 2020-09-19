package adapters.authentication

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.ktor.auth.jwt.*
import io.mockk.every
import io.mockk.mockk

class UserPrincipalTest : DescribeSpec({
    describe("User Principal test"){
        it("Transforms the JWT credentials into a UserPrincipal"){
            val jwtCredential = mockk<JWTCredential>{
                every { payload.subject } returns "1234"
                every { payload.getClaim("name") } returns mockk{
                    every { asString() } returns "John Doe"
                }
                every { payload.getClaim("userName") } returns mockk{
                    every {  asString() } returns "JohnDoe"
                }
                every { payload.getClaim("roles") } returns mockk{
                    every { asList(String::class.java) } returns listOf("User")
                }
            }

            val userPrincipal = jwtCredential.toUserPrincipal()

            userPrincipal.userId.shouldBe(1234)
            userPrincipal.name.shouldBe("John Doe")
            userPrincipal.userName.shouldBe("JohnDoe")
            userPrincipal.roles.shouldContain("User")
        }
    }
})