

type ilist is int | int[]

type rlist is real | int[]

function f(rlist e) -> bool:
    if e is int[]:
        return true
    else:
        return false

function g(ilist e) -> bool:
    return f((rlist) e)

public export method test() :
    assume f(1.0) == false
    assume f([1]) == true
    assume f([0;0]) == true
    assume g(1) == false
    assume g([1]) == true
    assume g([0;0]) == true
