

type expr is int[] | int

function f(expr e) -> bool:
    if e is int[]:
        return true
    else:
        return false

public export method test() :
    expr e = 1
    assume f(e) == false
    e = [1, 2, 3, 4]
    assume f(e) == true
