package config

data class ConfigurationFile(
    val api: ApiSettings,
    val database: DatabaseSettings
) {
    data class ApiSettings(
        val port: Int,
        val testing: Boolean
    )
    data class DatabaseSettings(
        val jdbcUrl: String
    )
}
