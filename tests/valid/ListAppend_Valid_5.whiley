import whiley.lang.*

type nat is (int n) where n >= 0

function f([nat] xs, [nat] ys, nat i) -> nat
requires i < (|xs| + |ys|):
    xs = xs ++ ys
    return xs[i]

method main(System.Console sys) -> void:
    [nat] left = [1, 2, 3]
    [nat] right = [5, 6, 7]
    nat r = f(left, right, 1)
    sys.out.println(r)
    r = f(left, right, 4)
    sys.out.println(r)
