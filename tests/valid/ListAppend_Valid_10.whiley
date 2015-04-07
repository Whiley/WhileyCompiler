import whiley.lang.*

function f([int] xs) -> int
requires no { x in xs | x < 0 }:
    return |xs|

method main(System.Console sys) -> void:
    [int] left = [1, 2, 3]
    [int] right = [5, 6, 7]
    int r = f(left ++ right)
    sys.out.println(r)
