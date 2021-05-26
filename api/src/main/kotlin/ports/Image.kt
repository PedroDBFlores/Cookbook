package ports

import java.io.InputStream

sealed class ImageState {
    class Valid(val imageStream: InputStream) : ImageState()
    class Resized(val result: ByteArray) : ImageState()
    object NotAnImage : ImageState()
}

fun interface ImageChecker {
    fun check(imageStream: InputStream): ImageState
}

fun interface ImageResizer {
    fun resize(
        validImage: ImageState.Valid,
        targetWidth: Int,
        targetHeight: Int,
    ): ImageState.Resized
}
