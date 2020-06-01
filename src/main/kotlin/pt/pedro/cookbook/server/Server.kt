package pt.pedro.cookbook.server

import com.papsign.ktor.openapigen.OpenAPIGen
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.request.path
import org.slf4j.event.Level

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.serverModule(testing: Boolean = false) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
//    install(HSTS) {
//        includeSubDomains = true
//    }

    // https://ktor.io/servers/features/https-redirect.html#testing
//    if (!testing) {
//        install(HttpsRedirect) {
//            // The port to redirect to. By default 443, the default HTTPS port.
//            sslPort = 443
//            // 301 Moved Permanently, or 302 Found redirect.
//            permanentRedirect = true
//        }
//    }

    install(OpenAPIGen){
        info {
            version = "1.0"
            title = "Cookbook API"
            description = "Cookbook API"
            contact {
                name = "Support"
                email = "support@test.com"
            }
        }
    }
}