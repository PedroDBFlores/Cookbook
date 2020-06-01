package utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

fun convertToJSON(obj: Any): String = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(obj)
