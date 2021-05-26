package adapters.validators

import ports.ImageState
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

fun checkImage(imageStream: InputStream): ImageState = runCatching {
    imageStream.cloneAsOutputStream()
        .run {
            ImageIO.read(ByteArrayInputStream(this.toByteArray())).type
            ImageState.Valid(imageStream)
        }
}.getOrElse {
    ImageState.NotAnImage
}

private fun InputStream.cloneAsOutputStream(): ByteArrayOutputStream {
    val streamClone = ByteArrayOutputStream()
    this.copyTo(streamClone)
    return streamClone
}
