package materials

import Hit
import math.Ray
import things.Scene
import math.times
import kotlin.math.pow

data class WhateverMaterial(val color: MaterialColor,
                            val ambient: MaterialColor = MaterialColor(0.1f, 0.1f, 0.1f),
                            val specular: Float = 100f
): Material {
    override fun shade(ray: Ray, hit: Hit, scene: Scene): MaterialColor {
        var color = this.color * ambient
        val light = scene.light
        val rayToLight = (hit.point + 0.0001f * hit.normal).rayTo(light.point)
        if (scene.intersects(rayToLight) == null) {

            val lambert = java.lang.Float.max(0f, hit.normal dot rayToLight.direction)
            color += lambert * light.color

            val h = (-ray.direction + rayToLight.direction).normalized()

            val intensity = (hit.normal dot h).pow(specular)

            color += intensity * MaterialColor(0.3f, 0f, 0f)
        }

        return color
    }
}