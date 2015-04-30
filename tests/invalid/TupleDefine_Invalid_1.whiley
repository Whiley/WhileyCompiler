type point is {real y, real x}

function f(int x) -> int:
    return x

method main() -> int:
    point p = {y: 2.23, x: 1.0}
    return f(p.y)
