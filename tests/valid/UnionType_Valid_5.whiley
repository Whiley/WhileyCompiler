import whiley.lang.*

type ur4nat is (int x) where x > 0

type tur4nat is (int x) where x > 10

type wur4nat is ur4nat | tur4nat

function f(wur4nat x) -> any:
    return x

method main(System.Console sys) -> void:
    sys.out.println(f(1))
