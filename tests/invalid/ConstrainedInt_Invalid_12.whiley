
type irf2nat is (int x) where x > 0

function f(irf2nat x) -> int:
    return x

function g(int x) :
    f(x)

method main() :
    g(-1)
