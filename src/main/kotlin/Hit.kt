import materials.Material
import math.Point
import math.Vector

data class Hit(val point: Point, val normal: Vector, val distance: Float, val material: Material)