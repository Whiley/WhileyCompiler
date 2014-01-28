import whiley.lang.System

type nat is (int x) where x >= 0

function f([int] xs) => [nat]
requires |xs| == 0:
    return xs

method main(System.Console sys) => void:
    [nat] rs = f([])
    sys.out.println(Any.toString(rs))
