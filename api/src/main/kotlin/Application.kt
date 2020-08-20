import com.sksamuel.hoplite.ConfigLoader
import config.ConfigurationFile
import server.CookbookApi

fun main() {
    val configuration: ConfigurationFile = ConfigLoader().loadConfigOrThrow("/configuration.json")
    CookbookApi(configuration = configuration)
}

