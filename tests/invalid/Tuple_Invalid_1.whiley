type nat is (int x) where x >= 0

type natpair is (nat, nat)

function min(natpair p) -> int:
    int x, int y = p
    if x > y:
        return y
    else:
        return x

method main() -> void:
    (int,int) p = (0, -1)
    int x = min(p)
