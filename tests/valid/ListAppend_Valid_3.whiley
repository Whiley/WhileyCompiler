import whiley.lang.*

type nat is (int n) where n >= 0

function f([nat] xs, [nat] ys) -> [nat]:
    return xs ++ ys

method main(System.Console sys) -> void:
    [nat] left = [1, 2, 3]
    [nat] right = [5, 6, 7]
    [nat] r = f(left, right)
    sys.out.println(r)
