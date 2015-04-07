import whiley.lang.*

type fr2nat is (int x) where x >= 0

function f(fr2nat x) -> int:
    return x

method main(System.Console sys) -> void:
    int y = 1
    sys.out.println(f(y))
