package adapters.converters

import ports.ImageState
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

fun resizeImage(
    validImage: ImageState.Valid,
    targetWidth: Int,
    targetHeight: Int,
): ImageState.Resized {
    with(validImage) {
        imageData.readAsImage().run {
            if (width < targetWidth || height < targetHeight)
                return ImageState.Resized(this.toByteArray("jpg"))

            return ImageState.Resized(
                scaleImage(targetWidth, targetHeight)
                    .toByteArray("jpg")
            )
        }
    }
}

private fun ByteArray.readAsImage() = ImageIO.read(this.inputStream())

private fun BufferedImage.scaleImage(
    targetWidth: Int,
    targetHeight: Int
): BufferedImage {
    return getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH).let { scaled ->
        BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB).run {
            graphics.drawImage(scaled, 0, 0, null)
            this
        }
    }
}

private fun RenderedImage.toByteArray(formatName: String): ByteArray {
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(this, formatName, outputStream)
    return outputStream.toByteArray()
}
