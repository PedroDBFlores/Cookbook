package adapters.validators

import ports.ImageState
import javax.imageio.ImageIO

fun checkImage(imageData: ByteArray): ImageState = runCatching {
    ImageIO.read(imageData.inputStream()).type
    ImageState.Valid(imageData)
}.getOrElse {
    ImageState.NotAnImage
}
