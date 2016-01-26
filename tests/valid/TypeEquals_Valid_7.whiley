

type intreal is real | int

function f(intreal e) -> bool:
    if e is int:
        return true
    else:
        return false

public export method test() :
    assume f(1) == true
    assume f(1.134) == false
    assume f(1.0) == false
