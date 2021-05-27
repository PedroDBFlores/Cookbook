package adapters.converters

import ports.ImageState
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun resizeImage(
    validImage: ImageState.Valid,
    targetWidth: Int,
    targetHeight: Int,
): ImageState.Resized = validImage.imageData.readAsImage().run {
    if (width < targetWidth || height < targetHeight) {
        ImageState.Resized(this.toByteArray("jpg"))
    } else {
        ImageState.Resized(
            scaleImage(targetWidth, targetHeight)
                .toByteArray("jpg")
        )
    }
}

private fun ByteArray.readAsImage() = ImageIO.read(this.inputStream())

private fun BufferedImage.scaleImage(
    targetWidth: Int,
    targetHeight: Int
): BufferedImage = getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH).let { scaled ->
    BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB).run {
        graphics.drawImage(scaled, 0, 0, null)
        this
    }
}

private fun RenderedImage.toByteArray(formatName: String): ByteArray =
    ByteArrayOutputStream().let { outputStream ->
        ImageIO.write(this, formatName, outputStream)
        outputStream.toByteArray()
    }
