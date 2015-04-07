import whiley.lang.*
type nat is (int x) where x >= 0
type neg is !nat

function f(neg x) -> (int y)
ensures y < 0:
    if x is int:
        return x
    else:
        return 0

public method main(System.Console sys) -> void:
    sys.out.println(f(-1))
    sys.out.println(f(-2))
