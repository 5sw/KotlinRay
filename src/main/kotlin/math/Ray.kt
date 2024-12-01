package math

data class Ray(val origin: Point, val direction: Vector) {
    fun at(t: Float) = origin + t * direction
}