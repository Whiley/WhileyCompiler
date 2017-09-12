type point is ({int y, int x} _this) where (_this.x > 0) && (_this.y > 0)

function f(point p) -> point:
    return p

public export method test():
    point p = {y: 1, x: -1}
    f(p)
