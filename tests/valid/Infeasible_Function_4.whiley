// Similar to Infeasible_Function_2, but mutual recursion.

function f(int a) -> (int r)
requires 0 < a
requires a <= 2
ensures (2 * r) == a:
    //
    int b = g(a)
    return b

function g(int a) -> (int r)
requires 0 < a
requires a <= 2
ensures (2 * r) == a:
    //
    int b = f(a)
    return b

public export method test():
    assert g(2) == 1
