function f({int x} rec) -> int:
    return rec.x

function f({int x, int y} rec) -> int:
    return rec.x + rec.y

function g({int x, int y} rec) -> int:
    return f(rec)

method test():
    {int x} r1 = {x:123}
    {int x, int y} r2 = {x:456, y:789}
    assume f(r1) == 123
    assume f(r2) == 1245
    assume g(r2) == 1245