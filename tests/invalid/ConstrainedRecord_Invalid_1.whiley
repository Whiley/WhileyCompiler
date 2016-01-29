
type tup is {int y, int x}

type point is ({int y, int x} this) where (this.x > 0) && (this.y > 0)

function f(point p) -> point:
    return p

method main(): 
    tup z = {y: -2, x: 1}
    point p = f(z)
