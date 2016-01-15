

function f(int x) -> int:
    return x + 1

type func_t is function (int) -> int

method g(int p) -> int:
    func_t func = &(int x -> f(x + 1))
    return func(p)

public export method test() :
    int x = g(5)
    assume x == 7
