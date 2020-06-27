package utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Transforms and returns a JSON object
 * @param source The source object
 * @return A JSON representation of the source object
 */
fun convertToJSON(source: Any): String =
    ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(source)

/**
 * Parses and removes a provided set of properties from a JSON object
 * @param source The source object
 * @param properties The properties to be removed from the JSON object
 * @return A JSON object without the provided properties
 */
fun removeJSONProperties(source: Any, vararg properties: String): String {
    val jsonNode = ObjectMapper().readTree(convertToJSON(source))
    with(jsonNode as ObjectNode) {
        remove(properties.toMutableList())
    }
    return jsonNode.toString()
}

/**
 * Creates a JSON object with simple key-value pairs (without nesting)
 * @param values The key-value pairs to be added to the JSON object
 * @return A JSON with the provided key-value pairs
 */
fun composeSimpleJsonObject(values: Map<String, String>): String {
    val objectNode = ObjectMapper().createObjectNode()
    for (mapValue in values) {
        with(mapValue) {
            objectNode.put(key, value)
        }
    }
    return objectNode.toString()
}