interface Material {
    fun shade(ray: Ray, hit: Hit, scene: Scene): MaterialColor
}