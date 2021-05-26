package adapters.converters

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import ports.ImageState
import javax.imageio.ImageIO

internal class ImageResizerTest : DescribeSpec({
    describe("Image resizer") {
        it("resizes an image to a specific size") {
            val source = this.javaClass.getResourceAsStream("/resize_test.png")!!
            val imageBytes = source.readBytes()

            val resizeResult = resizeImage(
                targetWidth = 200,
                targetHeight = 200,
                validImage = ImageState.Valid(imageBytes)
            )

            val resizedImage = ImageIO.read(resizeResult.result.inputStream())
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

                val resizeResult = resizeImage(
                    targetWidth = originalImage.width + widthAddition,
                    targetHeight = originalImage.height + heightAddition,
                    validImage = ImageState.Valid(imageBytes)
                )

                val resizedImage = ImageIO.read(resizeResult.result.inputStream())
                resizedImage.width.shouldBe(originalImage.width)
                resizedImage.height.shouldBe(originalImage.height)
            }
        }
    }
})
