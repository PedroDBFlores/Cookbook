package adapters.converters

import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception
import javax.imageio.ImageIO

fun resizeJpegImage(
    targetWidth: Int,
    targetHeight: Int,
    imageSourceStream: InputStream
): ByteArray {
    imageSourceStream.readAsImage().run {
        if (width < targetWidth || height < targetHeight)
            return this.toByteArray("jpg")

        return scaleImage(targetWidth, targetHeight)
            .toByteArray("jpg")
    }
}

private fun InputStream.readAsImage() = runCatching {
    ImageIO.read(this)
}.also {
    close()
}.getOrNull().let { it ?: throw UnsupportedImageFormat() }

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

class UnsupportedImageFormat : Exception()