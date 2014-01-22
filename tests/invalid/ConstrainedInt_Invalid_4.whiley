import println from whiley.lang.System

function f(int x) => int
requires x != 0
ensures $ != 1:
    return x

method main(System.Console sys) => void:
    sys.out.println(f(9))
