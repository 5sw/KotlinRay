data class MaterialColor(val r: Float, val g: Float, val b: Float) {
    fun toColor(): Color = Color(
        (r * UByte.MAX_VALUE.toFloat()).toInt().toUByte(),
        (g * UByte.MAX_VALUE.toFloat()).toInt().toUByte(),
        (b * UByte.MAX_VALUE.toFloat()).toInt().toUByte()
    )

    operator fun times(rhs: MaterialColor) = MaterialColor(r * rhs.r, g * rhs.g, b * rhs.b)
    operator fun plus(rhs: MaterialColor) = MaterialColor(r + rhs.r, g + rhs.g, b + rhs.b)

    companion object {
        val Black: MaterialColor = MaterialColor(0f, 0f, 0f)
    }
}