package config

data class ConfigurationFile(
    val api: ApiSettings,
    val jwt: JWTSettings,
    val database: DatabaseSettings
) {
    data class ApiSettings(
        val port: Int,
        val testing: Boolean
    )

    data class JWTSettings(
        val secret: String
    )

    data class DatabaseSettings(
        val driver: String,
        val jdbcUrl: String,
        val username: String,
        val password: String
    )
}
