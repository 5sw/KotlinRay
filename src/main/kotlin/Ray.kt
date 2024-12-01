import java.io.File
import java.lang.Float.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

data class Hit(val point: Point, val normal: Vector, val distance: Float, val material: Material)

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

operator fun Float.times(rhs: MaterialColor) = MaterialColor(this * rhs.r, this * rhs.g, this * rhs.b)

data class Ray(val origin: Point, val direction: Vector) {
    fun at(t: Float) = origin + t * direction
}

interface Thing {
    fun intersects(ray: Ray): Hit?
}

interface Material {
    fun shade(ray: Ray, hit: Hit, scene: Scene): MaterialColor
}

data class WhateverMaterial(val color: MaterialColor,
                            val ambient: MaterialColor = MaterialColor(0.1f, 0.1f, 0.1f),
                            val specular: Float = 100f
): Material {
    override fun shade(ray: Ray, hit: Hit, scene: Scene): MaterialColor {
        var color = this.color * ambient
        val light = scene.light
        val rayToLight = (hit.point + 0.0001f * hit.normal).rayTo(light.point)
        if (scene.intersects(rayToLight) == null) {

            val lambert = max(0f, dot(hit.normal, rayToLight.direction))
            color += lambert * light.color

            val h = (-ray.direction + rayToLight.direction).normalized()

            val intensity = dot(hit.normal, h).pow(specular)

            color += intensity * MaterialColor(0.3f, 0f, 0f)
        }

        return color
    }
}

data class ReflectiveMaterial(
                            val reflectivity: Float,
                            val color: MaterialColor,
                            val ambient: MaterialColor = MaterialColor(0.1f, 0.1f, 0.1f),
                            val specular: Float = 10f
): Material {
    override fun shade(ray: Ray, hit: Hit, scene: Scene): MaterialColor {
        var color = this.color * ambient
        val light = scene.light
        val offsetPoint = hit.point + 0.0001f * hit.normal
        val rayToLight = offsetPoint.rayTo(light.point)
        if (scene.intersects(rayToLight) == null) {

            val lambert = max(0f, dot(hit.normal, rayToLight.direction))
            color += lambert * light.color

            val h = (-ray.direction + rayToLight.direction).normalized()

            val intensity = (hit.normal dot h).pow(specular)

            color += intensity * MaterialColor(0.3f, 0f, 0f)
        }

        val reflectionDirection = ray.direction - 2 * (ray.direction dot hit.normal) * hit.normal
        val reflectedColor = scene.colorForRay(Ray(offsetPoint, reflectionDirection)) ?: MaterialColor.Black

        return  (1.0f - reflectivity) * color + reflectivity * reflectedColor
    }
}


data class Plane(val point: Point, val normal: Vector, val material: Material) : Thing {
    override fun intersects(ray: Ray): Hit? {
        val bottom = dot(normal, ray.direction)
        if (bottom == 0.0f) return null
        val t = dot(normal, point - ray.origin) / bottom
        if (t <= 0.0f) return null
        return Hit(ray.at(t), normal, t, material)
    }
}

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

fun dot(lhs: Vector, rhs: Vector): Float = lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z

data class PointLight(val point: Point, val color: MaterialColor)

data class Scene(val things: List<Thing>, val light: PointLight): Thing {
    override fun intersects(ray: Ray): Hit? {
        var closest: Hit? = null
        for (thing in things) {
            val hit = thing.intersects(ray)
            if (hit != null && (closest == null || hit.distance < closest.distance)) {
                closest = hit
            }
        }

        return closest
    }

    fun colorForRay(ray: Ray): MaterialColor? {
        val hit = intersects(ray)
        if (hit != null) {
            return hit.material.shade(ray, hit, this)
        }
        return null
    }
}

fun main() {
    val bmp = Bitmap(1000, 1000)
    val origin = Point(500f, 500f, -500f)

    val sphere = Sphere(Point(500f, 500f, 500f), radius = 500f, material = ReflectiveMaterial(reflectivity = 0.2f, MaterialColor(0f, 1.0f, 0f)))
    val sphere2 = Sphere(Point(0f, 500f, 50f), radius = 50f, material = ReflectiveMaterial(reflectivity = 0.2f, MaterialColor(1.0f, 0.0f, 0.1f)))

    val plane = Plane(
        Point(0f, -1000f, 0f),
        normal = Vector(0f, 1f, 0f),
        material = WhateverMaterial(MaterialColor(1f, 0.0f, 0.0f))
    )

    val leftPlane = Plane(
        Point(-1000f, 0f, 0f),
        Vector(1f, 0f, 0f),
        material = WhateverMaterial(MaterialColor(0.0f, 1f, 0.0f))
    )

    val light = PointLight(Point(1000f, 1000f, -500f), color = 0.2f * MaterialColor(0.9f, 0.9f, 0.9f))

    val scene = Scene(listOf(
        sphere,
        sphere2,
        plane,
        leftPlane
    ), light = light)


    for (y in 0 until bmp.height) {
        for (x in 0 until bmp.width) {
            val point = Point(x.toFloat(), y.toFloat(), 0f)
            val ray = origin.rayTo(point)

            val color = scene.colorForRay(ray)
            if (color != null) {
                bmp.setPixel(x, y, color.toColor())
            }
        }
    }
    File("test.tga").writeBitmap(bmp)
}
