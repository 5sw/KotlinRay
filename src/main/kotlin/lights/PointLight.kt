package lights

import materials.MaterialColor
import math.Point

data class PointLight(val point: Point, val color: MaterialColor) : Light