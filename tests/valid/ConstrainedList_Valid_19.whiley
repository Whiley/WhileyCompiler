import whiley.lang.*

type nat is (int x) where x >= 0

function g([nat] xs) -> [nat]:
    return xs

function f([nat] xs) -> [nat]:
    return g(xs)

method main(System.Console sys) -> void:
    [nat] rs = f([1, 2, 3])
    sys.out.println(rs)
