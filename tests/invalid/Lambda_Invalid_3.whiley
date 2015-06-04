type func_t is function(int)->int

function f(int x) -> int
requires x > 1:
    return x + 1

function g(int p) -> int
requires p >= 0:
    func_t func = &(int x -> f(x + 1))
    return func(p)
