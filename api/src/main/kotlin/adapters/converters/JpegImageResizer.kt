package adapters.converters

import ports.ImageResizer

class JpegImageResizer(val height: Int, val width: Int) : ImageResizer {
    override fun resize(currentImage: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }
}
