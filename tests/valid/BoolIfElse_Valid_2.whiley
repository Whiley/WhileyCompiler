

function f(bool b) -> int:
    if b:
        return 1
    else:
        return 0

public export method test() :
    assume f(true) == 1
    assume f(false) == 0
