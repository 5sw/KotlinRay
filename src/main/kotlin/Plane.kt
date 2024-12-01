data class Plane(val point: Point, val normal: Vector, val material: Material) : Thing {
    override fun intersects(ray: Ray): Hit? {
        val bottom = dot(normal, ray.direction)
        if (bottom == 0.0f) return null
        val t = dot(normal, point - ray.origin) / bottom
        if (t <= 0.0f) return null
        return Hit(ray.at(t), normal, t, material)
    }
}