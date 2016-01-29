

type fr4nat is int

function g(fr4nat x) -> fr4nat:
    return x + 1

function f(fr4nat x) -> int:
    return x

public export method test() :
    int y = 1
    assume f(g(y)) == 2
