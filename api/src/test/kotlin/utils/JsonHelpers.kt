package utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Contains functions that help on manipulating JSONs in the tests
 */
object JsonHelpers {
    /**
     * Transforms and returns a JSON object
     * @param source The source object
     * @return A JSON representation of the source object
     */
    fun convertToJSON(source: Any): String =
        ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(source)

    /**
     * Parses and removes a provided set of properties from a JSON object
     * @param source The source JSON
     * @param properties The properties to be removed from the JSON object
     * @return A JSON object without the provided properties
     */
    fun removeJSONProperties(json: String, vararg properties: String): String {
        val jsonNode = ObjectMapper().readTree(json)
        with(jsonNode as ObjectNode) {
            remove(properties.toMutableList())
        }
        return jsonNode.toString()
    }

    /**
     * Parses and adds new properties to an exisitng JSON object
     * @param json The current json file object
     * @param properties The properties to be inserted into the JSON object
     * @return A JSON object with tne new properties
     */
    fun addJSONProperties(json: String, properties: Map<String, Any>): String {
        val jsonNode = ObjectMapper().readTree(json)
        with(jsonNode as ObjectNode) {
            properties.forEach {
                jsonNode.putPOJO(it.key, it.value)
            }
        }
        return jsonNode.toString()
    }

    fun createJSONObject(properties: Map<String, Any>): String {
        val objectNode = ObjectMapper().createObjectNode()
        properties.forEach {
            objectNode.putPOJO(it.key, it.value)
        }
        return objectNode.toString()
    }

    //Extension methods
    fun Any.toJson() = convertToJSON(this)
    fun String.addPropertiesToJSON(properties: Map<String, Any>) = addJSONProperties(this, properties)
    fun String.removePropertiesFromJson(vararg properties: String) = removeJSONProperties(this, *properties)
}
