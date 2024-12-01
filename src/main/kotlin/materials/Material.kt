package materials

import Hit
import math.Ray
import things.Scene

interface Material {
    fun shade(ray: Ray, hit: Hit, scene: Scene): MaterialColor
}