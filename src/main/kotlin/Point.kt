data class Point(val x: Float, val y: Float, val z: Float) {
    operator fun plus(rhs: Vector) : Point = Point(x + rhs.x, y + rhs.y, z + rhs.z)
    operator fun minus(rhs: Point): Vector = Vector(x - rhs.x, y - rhs.y, z - rhs.z)

    fun rayTo(other: Point) = Ray(this, (other - this).normalized())
}
