import whiley.lang.*

type nat is (int n) where n >= 0

function f([int] xs, [int] ys) -> nat:
    return |xs ++ ys|

method main(System.Console sys) -> void:
    [int] left = [1, 2, 3]
    [int] right = [5, 6, 7]
    int r = f(left, right)
    sys.out.println(r)
