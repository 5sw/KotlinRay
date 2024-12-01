import java.io.File

operator fun Float.times(rhs: MaterialColor) = MaterialColor(this * rhs.r, this * rhs.g, this * rhs.b)

data class Ray(val origin: Point, val direction: Vector) {
    fun at(t: Float) = origin + t * direction
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
