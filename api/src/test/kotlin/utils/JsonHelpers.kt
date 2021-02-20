package utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 * Contains functions that help on manipulating JSONs in the tests
 */
object JsonHelpers {
    /**
     * Transforms and returns a JSON object
     * @param source The source object
     * @return A JSON representation of the source object
     */
    inline fun <reified T> T.toJson(): String = Json.encodeToString(this)

    fun createJSONObject(properties: Map<String, Any?>): String {
        val obj = buildJsonObject {
            properties.forEach { (k, v) ->
                when (v) {
                    is String -> put(k, Json.encodeToJsonElement(v))
                    is Int -> put(k, Json.encodeToJsonElement(v))
                    is Float -> put(k, Json.encodeToJsonElement(v))
                    is Boolean -> put(k, Json.encodeToJsonElement(v))
                    else -> Unit
                }
            }
        }
        return Json.encodeToString(obj)
    }

    fun createJSONObject(vararg properties: Pair<String, Any?>) = createJSONObject(mapOf(*properties))
    fun Map<String, Any?>.toJson(): String = createJSONObject(this)

    inline fun <reified T> String.transformInto() = Json.decodeFromString<T>(this)
}
