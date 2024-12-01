package things

import Hit
import math.Ray

interface Thing {
    fun intersects(ray: Ray): Hit?
}