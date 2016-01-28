

function f(bool x) -> int:
    switch x:
        case true:
            return 0
        case false:
            return -1
    return 10

public export method test() :
    assume f(true) == 0
    assume f(false) == -1
