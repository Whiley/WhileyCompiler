

function f(int | bool x) -> int:
    if x is int:
        return x
    else:
        return 1

public export method test() :
    assume f(true) == 1
    assume f(123) == 123
