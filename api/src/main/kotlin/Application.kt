import config.Modules
import web.CookbookApi

fun main() {
    with(Modules.cookbookApiDependencies) {
        CookbookApi(
            config = configurationFile,
            javalinPlugins = plugins,
            router = router
        ).start()
    }
}

