
type tup is {int y, int x}

type point is ({int y, int x} _this) where (_this.x > 0) && (_this.y > 0)

function f(point p) -> point:
    return p

method main(): 
    tup z = {y: -2, x: 1}
    point p = f(z)
