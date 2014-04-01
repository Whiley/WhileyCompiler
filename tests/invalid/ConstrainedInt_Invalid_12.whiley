
type irf2nat is int x where x > 0

function f(irf2nat x) => void:
    debug Any.toString(x)

function g(int x) => void:
    f(x)

method main(System.Console sys) => void:
    g(-1)
