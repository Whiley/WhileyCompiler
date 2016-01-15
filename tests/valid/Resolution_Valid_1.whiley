

function f(int b) -> int:
    return b + 1

public export method test() :
    int b = f(10)
    assume b == 11
