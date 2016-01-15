
type scf3nat is (int x) where x > 0

function f(scf3nat[] xs) -> int:
    return |xs|

method main() :
    int[] x = [1]
    x[0] = -1
    f(x)
