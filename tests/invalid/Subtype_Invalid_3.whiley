
type scf3nat is (int x) where x > 0

function f(scf3nat[] xs) -> int:
    return |xs|

public export method test() :
    int[] x = [1]
    x[0] = -1
    f((scf3nat[]) x)
