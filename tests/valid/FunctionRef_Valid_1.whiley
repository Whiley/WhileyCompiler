

function f1(int x) -> int:
    return x + 1

function f2(int x) -> int:
    return x * 2

type func_t is function(int) -> int

function g(func_t func) -> int:
    return func(1234)

public export method test() :
    assume g(&f1) == 1235
    assume g(&f2) == 2468
