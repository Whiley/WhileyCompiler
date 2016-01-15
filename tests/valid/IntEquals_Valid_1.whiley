

function f(int x, real y) -> bool:
    if ((real) x) == y:
        return true
    else:
        return false

public export method test() :
    assume f(1, 4.0) == false
    assume f(1, 4.2) == false
    assume f(0, 0.0) == true
