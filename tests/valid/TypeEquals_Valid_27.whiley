import whiley.lang.System

type Point is {int y, int x}

type Point3D is {int z, int y, int x}

type Points is Point | Point3D

method test(Points t) => void:
    if t is Point:
        debug "GOT POINT\n"
    else:
        debug "GOT POINT3D\n"

method main(System.Console sys) => void:
    Point3D p3d = {z: 3, y: 2, x: 1}
    test(p3d)
    Point p2d = {y: 2, x: 1}
    test(p2d)
