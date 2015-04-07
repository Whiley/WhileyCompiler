import whiley.lang.*

type fr3nat is (int x) where x >= 0

function f(int x) -> int:
    return x

method main(System.Console sys) -> void:
    int y = 1
    sys.out.println(f(y))
