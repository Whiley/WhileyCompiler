import whiley.lang.System

type fr2nat is (int x) where x >= 0

function f(fr2nat x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    int y = 1
    sys.out.println(f(y))
