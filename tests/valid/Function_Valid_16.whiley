

function f(int x) -> (int y)
// Return must be greater than input
ensures x < y:
    //
    return x + 1

function g(int x, int y) -> int
requires x > y:
    return x + y

public export method test() :
    int a = 2
    int b = 1
    if a < b:
        a = f(b)
    int x = g(a, b)
    assume x == 3
