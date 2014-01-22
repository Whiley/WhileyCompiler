import println from whiley.lang.System

type nat is (int n) where n >= 0

function f([nat] xs, [nat] ys) => [nat]:
    return xs ++ ys

method main(System.Console sys) => void:
    left = [1, 2, 3]
    right = [5, 6, 7]
    r = f(left, right)
    sys.out.println(Any.toString(r))
