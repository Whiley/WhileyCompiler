

function f(int x, int y) -> int
requires y != 0:
    return x / y

public export method test() :
    assume f(10, 2) == 5
    assume f(9, 3) == 3
    assume f(10, 3) == 3

