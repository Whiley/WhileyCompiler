import whiley.lang.*

type nat is (int x) where x >= 0

function f([nat] xs) -> ([int] rs)
requires |xs| > 0
ensures some { x in rs | x >= 0 }:
    //
    return xs

method main(System.Console sys) -> void:
    [int] rs = f([1, 2, 3])
    sys.out.println(rs)
