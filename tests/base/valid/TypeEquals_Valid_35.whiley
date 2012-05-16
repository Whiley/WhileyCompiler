import println from whiley.lang.System

define Point as {int x, int y, ...}
define Point3D as {int x, int y, int z}
define Points as Point|Point3D

void ::test(Points t):
    if t is Point3D:
        debug "GOT POINT3D\n"
    else:
        debug "GOT POINT\n"

void ::main(System.Console sys):
    p3d = {x:1, y:2, z:3}
    test(p3d)
    p2d = {x:1, y:2}
    test(p2d)
    p2d = {x:1, y:2, w:3}
    test(p2d)

