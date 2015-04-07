import whiley.lang.*

type cr1nat is (int x) where x < 10

function f(cr1nat x) -> int:
    int y = x
    return y

method main(System.Console sys) -> void:
    sys.out.println(f(9))
