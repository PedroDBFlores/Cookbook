package utils

inline fun <reified T> Map<String, Any>.getTyped(key: String): T = this[key] as T
