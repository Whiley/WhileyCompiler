import * from whiley.lang.System

type nat is (int x) where x >= 0

type natpair is (nat, nat)

function min(natpair p) -> int:
    (x, y) = p
    if x > y:
        return y
    else:
        return x

method main(System.Console sys) -> void:
    p = (0, -1)
    x = min(p)
