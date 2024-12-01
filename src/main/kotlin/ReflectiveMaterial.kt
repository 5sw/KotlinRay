import kotlin.math.pow

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

            val lambert = java.lang.Float.max(0f, dot(hit.normal, rayToLight.direction))
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