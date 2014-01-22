import * from whiley.lang.*

function f(int x) => int:
    return x

method main(System.Console sys) => void:
    x = 1
    x = f(x + 2.3)
