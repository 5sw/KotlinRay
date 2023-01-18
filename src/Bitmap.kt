import java.io.File
import java.io.OutputStream

const val BYTES_PER_PIXEL = 4

data class Color(val r: UByte, val g: UByte, val b: UByte, val alpha: UByte = UByte.MAX_VALUE)

class Bitmap(val width: Int, val height: Int) {
    val data = UByteArray(width * height * BYTES_PER_PIXEL)

    fun setPixel(x: Int, y: Int, color: Color) {
        val offset = (y * width + x) * BYTES_PER_PIXEL
        data[offset + 1] = color.g
        data[offset + 0] = color.b
        data[offset + 2] = color.r
        data[offset + 3] = color.alpha
    }

    fun getPixel(x: Int, y: Int): Color {
        val offset = (y * width + x) * BYTES_PER_PIXEL
        return Color(
            r = data[offset + 2],
            g = data[offset + 1],
            b = data[offset + 0],
            alpha = data[offset + 3]
        )
    }
}

fun File.writeBitmap(bitmap: Bitmap) {
    outputStream().use { stream ->
        stream.write(0) // ID length
        stream.write(0) // No color map
        stream.write(2) // Uncompressed true color image

        stream.writeWord(0) // Color map starts at index 0
        stream.writeWord(0) // No color map entries
        stream.write(0) // 0 bits per color map entry

        stream.writeWord(0) // X origin
        stream.writeWord(0) // Y origin
        stream.writeWord(bitmap.width.toShort())
        stream.writeWord(bitmap.height.toShort())
        stream.write(BYTES_PER_PIXEL * 8)
        stream.write(8)
        stream.write(bitmap.data.toByteArray())
    }
}

fun OutputStream.writeWord(word: Short) {
    write(word.toInt() and 0xFF)
    write((word.toInt() shr 8) and 0xFF)
}

