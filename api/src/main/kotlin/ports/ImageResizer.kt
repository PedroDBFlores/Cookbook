package ports

import java.io.InputStream

fun interface ImageResizer {
    fun resize(targetWidth: Int, targetHeight: Int, imageStream: InputStream): ByteArray
}
