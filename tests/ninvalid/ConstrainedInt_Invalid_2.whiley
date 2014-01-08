
type irf3nat is int where $ < 10

type pirf3nat is irf3nat where $ > 0

function f(int x) => pirf3nat:
    return x

method main(System.Console sys) => void:
    debug Any.toString(f(11))
