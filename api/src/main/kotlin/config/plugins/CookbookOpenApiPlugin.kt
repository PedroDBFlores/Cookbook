package config.plugins

import io.javalin.core.plugin.Plugin
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag

class CookbookOpenApiPlugin {
    operator fun invoke(): Plugin {
        val initialConfigurationCreator = {
            OpenAPI()
                .tags(listOf(Tag().name("Cookbook")))
                .info(Info().version("0.1").title("Cookbook API documentation").description("Documentation for public endpoints of Cookbook."))
                .addServersItem(Server().url("http://localhost:900"))
        }

        val openApiOptions = OpenApiOptions(initialConfigurationCreator)
            .path("/swagger-docs")
            .reDoc(ReDocOptions("/apidoc").title("Cookbook documentations"))


        return OpenApiPlugin(openApiOptions)
    }
}