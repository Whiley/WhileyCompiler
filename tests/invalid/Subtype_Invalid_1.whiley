
type scf1nat is (int n) where n >= 0

function f(scf1nat x) -> int:
    return x

public export method test() :
    int x = -1
    f((scf1nat) x)
