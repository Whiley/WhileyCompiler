

function f(real x) -> int:
    return 1

function f(int x) -> int:
    return 2

public export method test() :
    assume f(1) == 2
    assume f(1.23) == 1
