package ports

sealed class ImageState {
    class Valid(val imageData: ByteArray) : ImageState()
    class Resized(val result: ByteArray) : ImageState()
    object NotAnImage : ImageState()
}

fun interface ImageChecker {
    fun check(imageData: ByteArray): ImageState
}

fun interface ImageResizer {
    fun resize(
        validImage: ImageState.Valid,
        targetWidth: Int,
        targetHeight: Int,
    ): ImageState.Resized
}
