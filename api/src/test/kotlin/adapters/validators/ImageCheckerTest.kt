package adapters.validators

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import ports.ImageState

internal class ImageCheckerTest : DescribeSpec({
    describe("Image validator") {
        it("checks a valid image") {
            val imageStream = this.javaClass.getResourceAsStream("/resize_test.png")!!

            val result = checkImage(imageStream.readAllBytes())

            result.shouldBeInstanceOf<ImageState.Valid>()
        }

        it("returns not an image if it's not one") {
            val imageStream = this.javaClass.getResourceAsStream("/logback.xml")!!

            val result = checkImage(imageStream.readAllBytes())

            result.shouldBeInstanceOf<ImageState.NotAnImage>()
        }
    }
})
