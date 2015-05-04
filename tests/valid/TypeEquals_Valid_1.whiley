

type bop is {int y, int x} where x > 0

type expr is int | bop

function f(expr e) -> bool:
    if e is int:
        return true
    else:
        return false

public export method test() -> void:
    expr e = 1
    assume f(e) == true
    e = {y: 2, x: 1}
    assume f(e) == false
