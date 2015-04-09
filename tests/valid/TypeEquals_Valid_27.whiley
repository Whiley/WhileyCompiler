import whiley.lang.*

type Point is {int y, int x}

type Point3D is {int z, int y, int x}

type Points is Point | Point3D

method test(Points t) -> bool:
    if t is Point:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    Point3D p3d = {z: 3, y: 2, x: 1}
    assume test(p3d) == false
    Point p2d = {y: 2, x: 1}
    assume test(p2d) == true
