package config

import com.fasterxml.jackson.annotation.JsonProperty

internal data class ConfigurationFile(
    @JsonProperty("api") val api: ApiSettings,
    @JsonProperty("database") val database: DatabaseSettings
) {
    internal data class ApiSettings(
        @JsonProperty("port") val port: Int,
        @JsonProperty("testing", defaultValue = "false") val testing: Boolean
    )

    internal data class DatabaseSettings(
        @JsonProperty("driver") val driver: String,
        @JsonProperty("jdbcUrl") val jdbcUrl: String,
        @JsonProperty("username") val userName: String,
        @JsonProperty("password") val password: String
    )
}
