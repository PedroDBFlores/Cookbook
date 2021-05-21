package ports

import java.io.InputStream

typealias ImageResizer = (Int, Int, InputStream) -> ByteArray
