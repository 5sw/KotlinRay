import kotlin.math.min
import kotlin.math.sqrt

data class Sphere(val center: Point, val radius: Float, val material: Material) : Thing {
    override fun intersects(ray: Ray): Hit? {
        val l = center - ray.origin
        val tc = dot(l, ray.direction)
        if (tc < 0f) return null

        val d = l.squaredLength - tc * tc
        val radiusSquared = radius * radius

        if (d > radiusSquared) return null

        val thc = sqrt(radiusSquared - d)

        val t = min(tc - thc, tc + thc)

        val hitPoint = ray.at(t)
        return Hit(hitPoint, (hitPoint - center).normalized(), t, material)
    }
}