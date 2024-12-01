import kotlin.math.sqrt

data class Vector(val x: Float, val y: Float, val z: Float) {
    val length: Float get() = sqrt(squaredLength)
    val squaredLength: Float get() = x * x + y * y + z * z

    fun normalized(): Vector = this / length

    operator fun plus(rhs: Vector): Vector = Vector(x + rhs.x, y + rhs.y, z + rhs.z)
    operator fun times(rhs: Float): Vector = Vector(x * rhs, y * rhs, z * rhs)
    operator fun unaryMinus(): Vector = Vector(-x, -y, -z)
    operator fun minus(rhs: Vector): Vector = Vector(x - rhs.x, y - rhs.y, z - rhs.z)
    operator fun div(rhs: Float): Vector = Vector(x / rhs, y / rhs, z / rhs)

    infix fun dot(rhs: Vector): Float = x * rhs.x + y * rhs.y + z * rhs.z
}

operator fun Float.times(rhs: Vector): Vector = rhs * this
