package adapters.converters

import io.kotest.assertions.fail
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import javax.imageio.ImageIO

internal class JpegImageResizerTest : DescribeSpec({
    describe("Jpeg image resizer") {
        it("resizes an image to a specific size") {
            val source = this.javaClass.getResourceAsStream("/resize_test.png")!!
            val imageBytes = source.readBytes()

            val resizedByteArray = resizeJpegImage(
                targetWidth = 200,
                targetHeight = 200,
                imageSourceStream = imageBytes.inputStream()
            )

            val resizedImage = ImageIO.read(resizedByteArray.inputStream())
            resizedImage.width.shouldBe(200)
            resizedImage.height.shouldBe(200)
        }

        arrayOf(
            row(1, 0, "width"),
            row(0, 1, "height"),
        ).forEach { (widthAddition, heightAddition, desc) ->
            it("doesn't resize the image when $desc is bigger") {
                val source = this.javaClass.getResourceAsStream("/resize_test.png")!!
                val imageBytes = source.readBytes()
                val originalImage = ImageIO.read(imageBytes.inputStream())

                val resizedByteArray = resizeJpegImage(
                    targetWidth = originalImage.width + widthAddition,
                    targetHeight = originalImage.height + heightAddition,
                    imageSourceStream = imageBytes.inputStream()
                )

                val resizedImage = ImageIO.read(resizedByteArray.inputStream())
                resizedImage.width.shouldBe(originalImage.width)
                resizedImage.height.shouldBe(originalImage.height)
            }
        }

        it("throws UnsupportedImageFormat if it's not an supported format") {
            val source = this.javaClass.getResourceAsStream("/application.conf")!!
            val imageBytes = source.readBytes()

            runCatching {
                resizeJpegImage(
                    targetWidth = 200,
                    targetHeight = 200,
                    imageSourceStream = imageBytes.inputStream()
                )
            }.onFailure {
                it.shouldBeInstanceOf<UnsupportedImageFormat>()
            }.onSuccess {
                fail("Shouldn't be able to read a config file as an image")
            }
        }
    }
})

