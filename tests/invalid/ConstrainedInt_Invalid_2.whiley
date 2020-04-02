
type irf3nat is (int x) where x < 10

type pirf3nat is (irf3nat x) where x > 0

function f(int x) -> pirf3nat:
    return (pirf3nat) x

public export method test():
    assume f(11) == 11
