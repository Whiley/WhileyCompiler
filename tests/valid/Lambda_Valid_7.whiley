type nat is (int x) where x >= 0

function f(nat x) -> int
requires x > 0:
    return x + 1

type func_t is function(int)->int

function g(int p) -> int
requires p >= 0:
    func_t func = &(nat x -> f(x + 1))
    return func(p)

public export method test() :
    int x = g(5)
    assume x == 7
