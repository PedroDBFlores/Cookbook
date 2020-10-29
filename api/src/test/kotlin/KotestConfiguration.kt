import io.kotest.core.config.AbstractProjectConfig

class KotestConfiguration : AbstractProjectConfig() {
    override val includeTestScopePrefixes: Boolean?
        get() = false
}
