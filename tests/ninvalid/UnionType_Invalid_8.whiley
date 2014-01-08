
type urf1nat is int where $ > 0

type turf1nat is int where $ > 10

type wurf1nat is urf1nat | turf1nat

function f(wurf1nat x) => void:
    debug Any.toString(x)

function g(int x) => void:
    f(x)

method main(System.Console sys) => void:
    g(1)
    g(-1)
