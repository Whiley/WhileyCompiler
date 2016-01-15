
type scf1nat is (int n) where n >= 0

function f(scf1nat x) -> int:
    return x

method main() :
    int x = -1
    f(x)
