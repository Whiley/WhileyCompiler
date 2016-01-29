type point is ({int y, int x} this) where (this.x > 0) && (this.y > 0)

function f(point p) -> point:
    return p

method main():
    point p = {y: 1, x: -1}
    f(p)
