
type pos is (int p) where p > 0

type poslist is [pos]

type plt is pos | poslist

type nat is (int n) where n >= 0

type natlist is [nat]

type nlt is nat | natlist

function g(int y) -> nlt
requires y >= 0:
    return y

function f(int x) -> plt
requires x >= 0:
    return g(x)

method main(System.Console sys) -> void:
    debug Any.toString(f(0))
