type bop is ({int y, int x} r) where r.x > 0

type expr is int | bop

function f(expr e) -> (bool r):
    if e is int:
        return true
    else:
        return false

public export method test():
    expr e = 1
    assume f(e) == true
    e = {y: 2, x: 1}
    assume f(e) == false
