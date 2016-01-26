

type expr is int[] | bool

function f(expr e) -> bool:
    if e is int[]:
        return true
    else:
        return false

public export method test() :
    expr e = true
    assume f(e) == false
    e = [1, 2, 3, 4]
    assume f(e) == true
