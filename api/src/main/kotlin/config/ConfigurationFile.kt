package config

import com.fasterxml.jackson.annotation.JsonProperty

data class ConfigurationFile(
    @JsonProperty("api") val api: ApiSettings,
    @JsonProperty("database") val database: DatabaseSettings
) {
    data class ApiSettings(
        @JsonProperty("port") val port: Int,
        @JsonProperty("testing", defaultValue = "false") val testing: Boolean
    )

    data class DatabaseSettings(
        @JsonProperty("driver") val driver: String,
        @JsonProperty("jdbcUrl") val jdbcUrl: String,
        @JsonProperty("username") val userName: String,
        @JsonProperty("password") val password: String
    )
}
