

type IntPoint is {int y, int x, ...}

type RealPoint is {real y, real x, ...}

type Point is IntPoint | RealPoint

function sum(Point vp) -> real:
    if vp is IntPoint:
        return (real) (vp.x + vp.y)
    else:
        return vp.x + vp.y

public export method test() :
    Point vp = {y: 2, x: 1}
    assume sum(vp) == 3.0
    vp = {y: 2.34, x: 1.23}
    assume sum(vp) == 3.57
