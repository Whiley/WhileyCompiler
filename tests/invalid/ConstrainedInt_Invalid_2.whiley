
type irf3nat is (int x) where x < 10

type pirf3nat is (irf3nat x) where x > 0

function f(int x) => pirf3nat:
    return x

method main(System.Console sys) => void:
    debug Any.toString(f(11))
