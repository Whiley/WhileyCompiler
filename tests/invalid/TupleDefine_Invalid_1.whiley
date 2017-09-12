type point is {bool y, bool x}

function f(int x) -> int:
    return x

public export method test() -> int:
    point p = {y: false, x: true}
    return f(p.y)
