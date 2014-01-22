import println from whiley.lang.System

type nat is (int x) where x >= 0

function f([nat] xs) => [int]
requires |xs| > 0
ensures some { x in $ | x >= 0 }:
    return xs

method main(System.Console sys) => void:
    rs = f([1, 2, 3])
    sys.out.println(Any.toString(rs))
