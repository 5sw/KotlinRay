package things

import Hit
import materials.MaterialColor
import lights.PointLight
import math.Ray

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