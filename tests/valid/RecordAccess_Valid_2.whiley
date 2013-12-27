import println from whiley.lang.System

define IntPoint as {int x, int y}
define RealPoint as {real x, real y}

define Point as IntPoint | RealPoint

real sum(Point vp):
    return vp.x + vp.y

void ::main(System.Console sys):
    vp = {x:1, y:2}
    sys.out.println(sum(vp))
    vp = {x:1.23, y:2.34}
    sys.out.println(sum(vp))
