import java.io.File
import java.lang.Float.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

data class Hit(val point: Point, val normal: Vector, val color: MaterialColor)

data class MaterialColor(val r: Float, val g: Float, val b: Float) {
    fun toColor(): Color = Color(
        (r * UByte.MAX_VALUE.toFloat()).toInt().toUByte(),
        (g * UByte.MAX_VALUE.toFloat()).toInt().toUByte(),
        (b * UByte.MAX_VALUE.toFloat()).toInt().toUByte()
    )

    operator fun times(rhs: MaterialColor) = MaterialColor(r * rhs.r, g * rhs.g, b * rhs.b)
    operator fun plus(rhs: MaterialColor) = MaterialColor(r + rhs.r, g + rhs.g, b + rhs.b)
}
operator fun Float.times(rhs: MaterialColor) = MaterialColor(this * rhs.r, this * rhs.g, this * rhs.b)

data class Ray(val origin: Point, val direction: Vector) {
    fun at(t: Float) = origin + t * direction
}

data class Sphere(val center: Point, val radius: Float, val color: MaterialColor) {
    fun intersects(ray: Ray): Hit? {
        val l = center - ray.origin
        val tc = dot(l, ray.direction)
        if (tc < 0f) return null

        val d = l.squaredLength - tc * tc
        val radiusSquared = radius * radius

        if (d > radiusSquared) return null

        val thc = sqrt(radiusSquared - d)

        val t = min(tc - thc, tc + thc)


        val hitPoint = ray.at(t)
        return Hit(hitPoint, (hitPoint - center).normalized(), color)
    }
}

fun dot(lhs: Vector, rhs: Vector): Float = lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z

data class PointLight(val point: Point, val color: MaterialColor)

fun main() {
    val bmp = Bitmap(1000, 1000)
    val origin = Point(500f, 500f, -500f)

    val sphere = Sphere(Point(500f, 500f, 500f), radius = 500f, color = MaterialColor(0f, 1.0f, 0f))

    val ambient = MaterialColor(0.1f, 0.1f, 0.1f)

    val light = PointLight(Point(1000f, 1000f, -500f), color = MaterialColor(0.5f, 0.5f, 0.5f))
    val specular = 100f

    for (y in 0 until bmp.height) {
        for (x in 0 until bmp.width) {
            val point = Point(x.toFloat(), y.toFloat(), 0f)
            val ray = origin.rayTo(point)

            val hit = sphere.intersects(ray)
            if (hit != null) {
                var color = hit.color * ambient

                val rayToLight = hit.point.rayTo(light.point)
                if (sphere.intersects(rayToLight) == null) {

                    val lambert = max(0f, dot(hit.normal, rayToLight.direction))
                    color += lambert * light.color

                    val h = (-ray.direction + rayToLight.direction).normalized()

                    val intensity = dot(hit.normal, h).pow(specular)

                    color += intensity * MaterialColor(0.3f, 0f, 0f)
                }

                bmp.setPixel(x, y, color.toColor())
            }
        }
    }
    File("test.tga").writeBitmap(bmp)
}
