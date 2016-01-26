
type scf4list is (int[] list) where |list| > 0

function f(scf4list x) -> int:
    return 1

method main() :
    int[] x = [0;0]
    f(x)
