import whiley.lang.*

type ir1nat is int

type pir1nat is ir1nat

function f(int x) -> int:
    if x > 2:
        int y = x
        return y
    return 0

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
