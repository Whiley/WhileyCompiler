import whiley.lang.*

type IntPoint is {int y, int x, ...}

type RealPoint is {real y, real x, ...}

type Point is IntPoint | RealPoint

function sum(Point vp) -> real:
    RealPoint rp = (RealPoint) vp
    return rp.x + rp.y

method main(System.Console sys) -> void:
    Point vp = {y: 2, x: 1}
    sys.out.println(sum(vp))
    vp = {y: 2.34, x: 1.23}
    sys.out.println(sum(vp))
