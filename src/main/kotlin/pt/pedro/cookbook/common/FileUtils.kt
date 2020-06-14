package pt.pedro.cookbook.common

import java.io.BufferedReader

/**
 * Holds common utilities for file management in the application
 */
object FileUtils {
    fun readResourceAsString(fileName: String): String {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)
        val reader = BufferedReader(stream.reader())
        return reader.use { r ->
            r.readText()
        }
    }
}