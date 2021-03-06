package ports

fun interface ImageResizer {
    fun resize(currentImage: ByteArray): ByteArray
}