import whiley.lang.System

function f(int x) => (int y)
requires x != 0
ensures y != 1:
    //
    return x

method main(System.Console sys) => void:
    sys.out.println(f(9))
