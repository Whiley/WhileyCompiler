

type PointAny is {int y, int x, ...}

type Point2D is {int y, int x}

type Point3D is {int z, int y, int x}

type Point is PointAny | Point2D | Point3D

function sum(Point vp) -> real:
    if vp is Point2D:
        return (real) (vp.x + vp.y)
    else:
        if vp is Point3D:
            return (real) (vp.x + vp.y + vp.z)
        else:
            return (real) (vp.x + vp.y)

public export method test() :
    Point vp = {y: 2, x: 1}
    assume sum(vp) == 3.0
    vp = {z: 2, y: 2, x: 1}
    assume sum(vp) == 5.0
    vp = {y: 2, h: 2, x: 1}
    assume sum(vp) == 3.0
