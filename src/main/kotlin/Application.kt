import config.Dependencies
import web.CookbookApi

fun main() {
    with(Dependencies) {
        CookbookApi(
            port = configuration.api.port,
            recipeTypeDependencies = recipeTypeDependencies,
            recipeDependencies = recipeDependencies,
            plugins = javalinPlugins
        ).start()
    }
}
