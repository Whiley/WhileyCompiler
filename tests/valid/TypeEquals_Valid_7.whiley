

type intbool is bool | int

function f(intbool e) -> bool:
    if e is int:
        return true
    else:
        return false

public export method test() :
    assume f(1) == true
    assume f(false) == false
    assume f(true) == false
