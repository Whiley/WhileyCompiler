
type scf4list is (int[] list) where |list| > 0

function f(scf4list x) -> int:
    return 1

public export method test() :
    int[] x = [0;0]
    f((scf4list) x)
