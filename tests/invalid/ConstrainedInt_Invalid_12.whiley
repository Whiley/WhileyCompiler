
type irf2nat is (int x) where x > 0

function f(irf2nat x) -> int:
    return x

function g(int x) :
    f((irf2nat) x)

public export method test() :
    g(-1)
