import * from whiley.lang.*

function f([int] ls) => [int]:
    return ls

method main(System.Console sys) => void:
    xs = [1, 2]
    xs[0] = 1.23
    f(xs)
