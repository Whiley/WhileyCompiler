import println from whiley.lang.System

define PointAny as {int x, int y, ...}
define Point2D as {int x, int y}
define Point3D as {int x, int y, int z}

define Point as PointAny | Point2D | Point3D

real sum(Point vp):
    if vp is Point2D:
        return vp.x + vp.y
    else if vp is Point3D:
        return vp.x + vp.y + vp.z
    else:
        // any
        return vp.x + vp.y

void ::main(System.Console sys):
    vp = {x:1, y:2}
    sys.out.println(sum(vp))
    vp = {x:1, y:2, z:2}
    sys.out.println(sum(vp))
    vp = {x:1, y:2, h:2}
    sys.out.println(sum(vp))
